package com.pelanggan.pelanggan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pelanggan.pelanggan.model.Pelanggan;
import com.pelanggan.pelanggan.repository.PelangganRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PelangganService {

    @Autowired
    private PelangganRepository PelangganRepository;

    private static final Logger log =
        LoggerFactory.getLogger(PelangganService.class);

    public List<Pelanggan> getAllPelanggan(){
        log.info("Mengambil semua data pelanggan");
        List<Pelanggan> list = PelangganRepository.findAll();
        log.info("Berhasil mengambil {} data pelanggan", list.size());
        return list;
    }

    public Pelanggan getPelangganById(long id){
        log.info("Mengambil data pelanggan dengan ID: {}", id);
        Pelanggan pelanggan = PelangganRepository.findById(id).orElse(null);
        log.info("Status pencarian ID {}: {}", id, (pelanggan != null ? "Ditemukan" : "Tidak Ditemukan"));
        return pelanggan;
    }
    
    public Pelanggan createPelanggan(Pelanggan Pelanggan){
        log.info("Menyimpan data pelanggan baru");
        Pelanggan savedPelanggan = PelangganRepository.save(Pelanggan);
        log.info("Berhasil menyimpan pelanggan dengan ID: {}", savedPelanggan.getId());
        return savedPelanggan;
    }

    public void deletePelanggan(long id) {
        log.info("Menghapus data pelanggan dengan ID: {}", id);
        PelangganRepository.deleteById(id);
        log.info("Berhasil menghapus data pelanggan dengan ID: {}", id);
    }

}