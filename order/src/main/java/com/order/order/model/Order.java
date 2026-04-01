package com.order.order.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "orders")
public class Order {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    private Long produkId; 
    // private Long id_pelanggan;

    private String tanggal; 

    // private double harga;
    private Integer jumlah; 
    private Double total;
}

