package com.order.order.service;

import java.util.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.order.order.model.Order;
import com.order.order.repository.OrderRepository;
import com.order.order.vo.Produk;
import com.order.order.vo.ResponseTemplate;
import org.springframework.web.client.HttpClientErrorException;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final Logger log =
        LoggerFactory.getLogger(OrderService.class);

     // 1. AMBIL SEMUA ORDER
    public List<Order> getAllOrders() {
        log.info("Memulai pencarian semua data order dari database");
        return orderRepository.findAll();
    }

    // 2. BUAT ORDER BARU (LOGIC UTAMA)
    public Order createOrder(Order order) {
        log.info("Memulai proses pembuatan order baru untuk Produk ID: {} dan Pelanggan ID: {}", order.getProdukId(), order.getPelangganId());
        
        // --- A. AMBIL DATA PRODUK ---
        String namaProduk;
        double hargaProduk;

        try {
            String urlProduk = "http://PRODUK/api/produk/" + order.getProdukId();
            log.info("Mengirim permintaan HTTP GET ke service PRODUK: {}", urlProduk);
            Produk produk = restTemplate.getForObject(urlProduk, Produk.class);

            if (produk == null) {
                log.info("Respon dari service PRODUK kosong untuk ID: {}", order.getProdukId());
                throw new IllegalArgumentException("Produk dengan ID " + order.getProdukId() + " tidak ditemukan");
            }
            namaProduk = produk.getNama();
            hargaProduk = produk.getHarga();
            log.info("Berhasil mengambil data produk: {} dengan harga: {}", namaProduk, hargaProduk);

        } catch (HttpClientErrorException.NotFound e) {
            log.info("Service PRODUK merespon NOT_FOUND untuk ID: {}", order.getProdukId());
            throw new IllegalArgumentException("Produk dengan ID " + order.getProdukId() + " tidak ditemukan");

        } catch (IllegalArgumentException e) {
            throw e; 

        } catch (Exception e) {
            log.info("Terjadi gangguan koneksi atau error pada service PRODUK: {}", e.getMessage());
            System.out.println("⚠️ Gagal ambil data produk: " + e.getMessage());
            throw new RuntimeException("Service PRODUK sedang tidak bisa dihubungi, coba lagi nanti");
        }

        // --- B. AMBIL DATA PELANGGAN ---
        String namaPelanggan;
        try {
            String urlCust = "http://PELANGGAN/api/pelanggan/" + order.getPelangganId();
            log.info("Mengirim permintaan HTTP GET ke service PELANGGAN: {}", urlCust);
            Map pelanggan = restTemplate.getForObject(urlCust, Map.class);

            if (pelanggan == null || pelanggan.get("nama") == null) {
                log.info("Respon data pelanggan kosong atau nama tidak ditemukan untuk ID: {}", order.getPelangganId());
                throw new IllegalArgumentException("Pelanggan dengan ID " + order.getPelangganId() + " tidak ditemukan");
            }
            namaPelanggan = pelanggan.get("nama").toString();
            log.info("Berhasil mengambil data pelanggan: {}", namaPelanggan);

        } catch (HttpClientErrorException.NotFound e) {
            log.info("Service PELANGGAN merespon NOT_FOUND untuk ID: {}", order.getPelangganId());
            throw new IllegalArgumentException("Pelanggan dengan ID " + order.getPelangganId() + " tidak ditemukan");

        } catch (IllegalArgumentException e) {
            throw e;

        } catch (Exception e) {
            log.info("Terjadi gangguan koneksi atau error pada service PELANGGAN: {}", e.getMessage());
            System.out.println("⚠️ Gagal ambil data pelanggan: " + e.getMessage());
            throw new RuntimeException("Service PELANGGAN sedang tidak bisa dihubungi, coba lagi nanti");
        }

        // --- C. ISI DATA OTOMATIS ---
        log.info("Mengisi kalkulasi harga, total belanja, dan tanggal otomatis ke objek order");
        order.setHarga(hargaProduk);
        order.setTotal(hargaProduk * order.getJumlah());
        order.setTanggal(new Date().toString());
        if (order.getStatus() == null) {
            order.setStatus("Selesai");
        }

        // --- D. SIMPAN KE DATABASE ---
        log.info("Menyimpan objek order baru ke database");
        Order savedOrder = orderRepository.save(order);
        log.info("Order berhasil disimpan dengan ID: {}", savedOrder.getId());

        // --- E. KIRIM KE RABBITMQ (jangan sampai gagalkan order kalau ini error) ---
        try {
            log.info("Menyiapkan payload pesan untuk dikirim ke RabbitMQ queue 'myQueue'");
            Map<String, Object> message = new HashMap<>();
            message.put("idOrder", savedOrder.getId());
            message.put("produkId", savedOrder.getProdukId());
            message.put("namaProduk", namaProduk);
            message.put("namaPelanggan", namaPelanggan);
            message.put("jumlahDipesan", savedOrder.getJumlah());
            message.put("total", savedOrder.getTotal());
            message.put("status", savedOrder.getStatus());
            message.put("tanggal", savedOrder.getTanggal());

            rabbitTemplate.convertAndSend("myQueue", message);
            log.info("Pesan order berhasil diteruskan ke RabbitMQ queue 'myQueue'");
            System.out.println("✅ Order Berhasil: " + namaProduk + " dipesan oleh " + namaPelanggan);
        } catch (Exception e) {
            log.info("Gagal meneruskan pesan ke RabbitMQ: {}", e.getMessage());
            System.out.println("⚠️ Order tersimpan tapi gagal kirim notifikasi ke RabbitMQ: " + e.getMessage());
        }

        return savedOrder;
    }

    // 3. UPDATE ORDER
    @Transactional
    public void update(Long id, Integer jumlah, String tanggal, String status) {
        log.info("Memulai proses update data order ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.info("Proses update gagal karena order ID {} tidak ditemukan", id);
                    return new IllegalStateException("Order tidak ada");
                });

        if (jumlah != null) {
            log.info("Mengubah jumlah order ID {} menjadi: {}", id, jumlah);
            order.setJumlah(jumlah);
        }
        if (tanggal != null && !tanggal.isEmpty()) {
            log.info("Mengubah tanggal order ID {} menjadi: {}", id, tanggal);
            order.setTanggal(tanggal);
        }
        if (status != null && !status.isEmpty()) {
            log.info("Mengubah status order ID {} menjadi: {}", id, status);
            order.setStatus(status);
        }
        
        orderRepository.save(order);
        log.info("Perubahan data order ID {} berhasil disimpan ke database", id);
    }

    // 4. GET BY ID
    public Order getOrderById(Long id) {
        log.info("Mencari data order di database berdasarkan ID: {}", id);
        return orderRepository.findById(id).orElse(null);
    }

    // 5. DELETE
    public void deleteOrder(Long id) {
        log.info("Menghapus data order dari database dengan ID: {}", id);
        orderRepository.deleteById(id);
        log.info("Data order dengan ID {} berhasil dihapus", id);
    }
}
