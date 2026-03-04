package com.pelanggan.pelanggan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pelanggan.pelanggan.model.Pelanggan;
import com.pelanggan.pelanggan.service.PelangganService;

@RestController
@RequestMapping("/api/pelanggan")
public class PelangganController {

    @Autowired
    private PelangganService PelangganService;

    @GetMapping
    public List<Pelanggan> getAllPelanggan() {
        return PelangganService.getAllPelanggan();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pelanggan> getPelangganById(@PathVariable Long id) {
        Pelanggan Pelanggan = PelangganService.getPelangganById(id);
        return Pelanggan != null ? ResponseEntity.ok(Pelanggan) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Pelanggan createPelanggan(@RequestBody Pelanggan Pelanggan) {
        return PelangganService.createPelanggan(Pelanggan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePelanggan(@PathVariable Long id) {
        PelangganService.deletePelanggan(id);
        return ResponseEntity.ok().build();
    }
}