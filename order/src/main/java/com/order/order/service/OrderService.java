package com.order.order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.order.order.model.Order;
import com.order.order.repository.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository produkRepository;

    public List<Order> getAllProduk(){
        return produkRepository.findAll();
    }

    public Order getProdukById(long id){
        return produkRepository.findById(id).orElse(null);
    }
    
    public Order createProduk(Order produk){
        return produkRepository.save(produk);
    }

    public void deleteProduk(long id) {
        produkRepository.deleteById(id);
    }
}