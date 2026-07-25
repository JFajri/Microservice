package com.produk.produk.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.produk.produk.model.Produk;
import com.produk.produk.service.ProdukService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/produk")
public class ProdukController {

    private static final Logger log =
        LoggerFactory.getLogger(ProdukController.class);

    @Autowired
    private ProdukService produkService;

    @GetMapping
    public List<Produk> getAllProduk() {
        log.info("Mengambil seluruh data produk");
        return produkService.getAllProduk();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produk> getProdukById(@PathVariable Long id) {

        log.info("Mengambil produk id={}", id);

        Produk produk = produkService.getProdukById(id);

        return produk != null ? ResponseEntity.ok(produk) : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public Produk createProduk(@RequestBody Produk produk) {

        log.info("Menambahkan produk baru");

        return produkService.createProduk(produk);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduk(@PathVariable Long id) {

        log.info("Menghapus produk id={}", id);

        produkService.deleteProduk(id);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/reduce-stok/{id}/{jumlah}")
    public ResponseEntity<?> reduceStok(@PathVariable Long id, @PathVariable int jumlah) {

        log.info("Mengurangi stok produk id={} sebanyak {}", id, jumlah);

        boolean success = produkService.reduceStok(id, jumlah);

        if (success) {
            return ResponseEntity.ok("Berhasil kurangi stok");
        } else {
            return ResponseEntity.badRequest().body("Gagal: Stok tidak cukup atau ID salah");
        }
    }


}