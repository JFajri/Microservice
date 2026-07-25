package com.pelanggan.pelanggan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pelanggan.pelanggan.model.Pelanggan;
import com.pelanggan.pelanggan.service.PelangganService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/pelanggan")
public class PelangganController {

    @Autowired
    private PelangganService PelangganService;

    private static final Logger log =
        LoggerFactory.getLogger(PelangganController.class);

    @GetMapping
    public List<Pelanggan> getAllPelanggan() {
        log.info("Mengambil seluruh data pelanggan");
        return PelangganService.getAllPelanggan();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pelanggan> getPelangganById(@PathVariable Long id) {
        Pelanggan Pelanggan = PelangganService.getPelangganById(id);
        log.info("Mengambil pelanggan id={}", id);
        return Pelanggan != null ? ResponseEntity.ok(Pelanggan) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Pelanggan createPelanggan(@RequestBody Pelanggan Pelanggan) {
        log.info("Menambahkan pelanggan baru");
        return PelangganService.createPelanggan(Pelanggan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePelanggan(@PathVariable Long id) {
        log.info("Menghapus produk id={}", id);
        PelangganService.deletePelanggan(id);
        return ResponseEntity.ok().build();
    }
}