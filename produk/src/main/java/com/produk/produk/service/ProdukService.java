package com.produk.produk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.produk.produk.model.Produk;
import com.produk.produk.repository.ProdukRepository;

@Service
public class ProdukService {

    @Autowired
    private ProdukRepository produkRepository;

    public List<Produk> getAllProduk(){
        return produkRepository.findAll();
    }

    public Produk getProdukById(long id){
        return produkRepository.findById(id).orElse(null);
    }
    
    public Produk createProduk(Produk produk){
        return produkRepository.save(produk);
    }

    public void deleteProduk(long id) {
        produkRepository.deleteById(id);
    }

public boolean reduceStok(Long id, int jumlah) {
    Produk produk = produkRepository.findById(id).orElse(null);
    if (produk != null) {
        // Gunakan double jika di database tipenya double
        double stokSekarang = produk.getStok(); 
        if (stokSekarang >= jumlah) {
            produk.setStok(stokSekarang - jumlah);
            produkRepository.save(produk);
            return true;
        }
    }
    return false;
}


}