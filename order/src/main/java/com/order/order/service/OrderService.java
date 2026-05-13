package com.order.order.service;

import java.util.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.order.order.model.Order;
import com.order.order.repository.OrderRepository;
import com.order.order.vo.Produk;
import com.order.order.vo.ResponseTemplate;

import jakarta.transaction.Transactional;

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

    // 1. AMBIL SEMUA ORDER
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 2. BUAT ORDER BARU (LOGIC UTAMA)
    public Order createOrder(Order order) {
        // --- A. AMBIL DATA PRODUK ---
        String namaProduk = "Produk Tidak Dikenal";
        double hargaProduk = 0.0;
        
        try {
            // Kita panggil pakai nama service PRODUK (sesuai Eureka kamu)
            String urlProduk = "http://PRODUK/api/produk/" + order.getProdukId();
            Produk produk = restTemplate.getForObject(urlProduk, Produk.class);
            
            if (produk != null) {
                namaProduk = produk.getNama();
                hargaProduk = produk.getHarga();
            }
        } catch (Exception e) {
            System.out.println("⚠️ Gagal ambil data produk dari service PRODUK: " + e.getMessage());
            // Jika produk dasar saja tidak ada, kita gagalkan ordernya
            throw new RuntimeException("Gagal buat order: Service PRODUK tidak merespon atau ID salah.");
        }

        // --- B. AMBIL DATA PELANGGAN ---
        String namaPelanggan = "Pelanggan Umum"; 
        try {
            String urlCust = "http://PELANGGAN/api/pelanggan/" + order.getPelangganId();
            Map pelanggan = restTemplate.getForObject(urlCust, Map.class);
            if (pelanggan != null && pelanggan.get("nama") != null) {
                namaPelanggan = pelanggan.get("nama").toString();
            }
        } catch (Exception e) {
            System.out.println("⚠️ Gagal ambil data pelanggan: " + e.getMessage());
            // Kita lanjut saja pakai nama default agar tidak error 500
        }

        // --- C. ISI DATA OTOMATIS ---
        order.setHarga(hargaProduk);
        order.setTotal(hargaProduk * order.getJumlah());
        order.setTanggal(new Date().toString());
        
        if (order.getStatus() == null) {
            order.setStatus("Selesai");
        }

        // --- D. SIMPAN KE DATABASE ---
        Order savedOrder = orderRepository.save(order);

        // --- E. KIRIM PAKET LENGKAP KE RABBITMQ ---
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
        System.out.println("✅ Order Berhasil: " + namaProduk + " dipesan oleh " + namaPelanggan);

        return savedOrder;
    }

    // 3. UPDATE ORDER
    @Transactional
    public void update(Long id, Integer jumlah, String tanggal, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Order tidak ada"));

        if (jumlah != null) order.setJumlah(jumlah);
        if (tanggal != null && !tanggal.isEmpty()) order.setTanggal(tanggal);
        if (status != null && !status.isEmpty()) order.setStatus(status);
        
        orderRepository.save(order);
    }

    // 4. GET BY ID
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    // 5. DELETE
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
