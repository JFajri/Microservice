package com.order.order.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.order.order.model.Order;
import com.order.order.repository.OrderRepository;
import com.order.order.vo.Produk;
import com.order.order.vo.ResponseTemplate;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import jakarta.transaction.Transactional;

@Service
public class OrderService {
    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public void update(Long orderId, Integer jumlah, String tanggal, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("Order tidak ada"));
        if (jumlah != null) {
            order.setJumlah(jumlah);
        }
        if (tanggal != null && tanggal.length() > 0
            && !Objects.equals(order.getTanggal(), tanggal)) {
                order.setTanggal(tanggal);
            }
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<ResponseTemplate> getOrderWithProdukById(Long id) {
        List<ResponseTemplate> responseList = new ArrayList<>();
        Order order = getOrderById(id);
        ServiceInstance serviceInstance = discoveryClient.getInstances("PRODUK").get(0);
        String url = serviceInstance.getUri().toString() + "/api/produk/" + order.getProdukId();
        Produk produk = restTemplate.getForObject(url, Produk.class);
        ResponseTemplate vo = new ResponseTemplate();
        vo.setOrder(order);
        vo.setProduk(produk);
        responseList.add(vo);
        return responseList;
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
