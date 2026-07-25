package com.produk.produk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.produk.produk.controller.ProdukController;
import com.produk.produk.model.Produk;
import com.produk.produk.repository.ProdukRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class ProdukService {

    @Autowired
    private ProdukRepository produkRepository;

    private static final Logger log =
        LoggerFactory.getLogger(ProdukService.class);

    public List<Produk> getAllProduk(){
        log.info("Mengambil data produk dari database");
        return produkRepository.findAll();
    }

    public Produk getProdukById(long id){
        log.info("Mencari produk id={}", id);
        return produkRepository.findById(id).orElse(null);
    }
    
    public Produk createProduk(Produk produk){
        log.info("Menyimpan produk {}", produk.getNama());
        return produkRepository.save(produk);
    }

    public void deleteProduk(long id) {
        log.info("Menghapus data produk");
        produkRepository.deleteById(id);
        
    }
    public boolean reduceStok(Long id, int jumlah) {
        Produk produk = produkRepository.findById(id).orElse(null);
        log.info("Mengurangi stok produk id={} sebanyak {}", id, jumlah);
        if (produk != null) {
            // Gunakan double jika di database tipenya double
            double stokSekarang = produk.getStok(); 
            if (stokSekarang >= jumlah) {
                produk.setStok(stokSekarang - jumlah);
                produkRepository.save(produk);
                return true;
            }
            log.info("Pengurangan stok berhasil");
        }
        log.warn("Stok tidak mencukupi");
        return false;
    }


}