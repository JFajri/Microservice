package com.fajri.produk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajri.produk.model.Produk;
import com.fajri.produk.repository.ProdukRepository;

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
}