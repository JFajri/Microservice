package com.order.order.vo;

import lombok.Data; // Import ini

@Data // Tambahkan anotasi ini
public class Produk {
    private Long id;
    private String nama;
    private String satuan;
    private double harga;
    private int stok; // Tambahkan ini juga supaya bisa cek stok nantinya
}
