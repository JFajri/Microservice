package com.pelanggan.pelanggan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pelanggan.pelanggan.model.Pelanggan;
import com.pelanggan.pelanggan.repository.PelangganRepository;

@Service
public class PelangganService {

    @Autowired
    private PelangganRepository PelangganRepository;

    public List<Pelanggan> getAllPelanggan(){
        return PelangganRepository.findAll();
    }

    public Pelanggan getPelangganById(long id){
        return PelangganRepository.findById(id).orElse(null);
    }
    
    public Pelanggan createPelanggan(Pelanggan Pelanggan){
        return PelangganRepository.save(Pelanggan);
    }

    public void deletePelanggan(long id) {
        PelangganRepository.deleteById(id);
    }
}