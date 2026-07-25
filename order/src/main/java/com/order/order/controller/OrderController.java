package com.order.order.controller;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.order.order.model.Order;
import com.order.order.service.OrderService;
import com.order.order.vo.ResponseTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    private static final Logger log =
        LoggerFactory.getLogger(OrderController.class);

    @GetMapping
    public List<Order> getAllOrders() {
        log.info("Menerima permintaan untuk mengambil semua order");
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable("id") Long id) {
        log.info("Menerima permintaan untuk mengambil order dengan ID: {}", id);
        return orderService.getOrderById(id);
    }

    //@GetMapping("/produk/{id}")
    //public List<ResponseTemplate> getOrderWithProdukId(@PathVariable("id") Long id) {
    //    return orderService.getOrderWithProdukById(id);
    //}

    @PutMapping("/{id}")
    public void updateOrder(@PathVariable("id") Long id,
            @RequestParam(required = false) int jumlah,
            @RequestParam(required = false) String tanggal,
            @RequestParam(required = false) String status) {
        log.info("Menerima permintaan update order ID: {}, jumlah: {}, tanggal: {}, status: {}", id, jumlah, tanggal, status);
        orderService.update(id, jumlah, tanggal, status);
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        log.info("Menerima permintaan untuk membuat order baru: {}", order);
        return orderService.createOrder(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        log.info("Menerima permintaan untuk menghapus order dengan ID: {}", id);
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }

}