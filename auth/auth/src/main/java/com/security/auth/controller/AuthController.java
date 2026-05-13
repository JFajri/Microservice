package com.security.auth.controller;

import com.security.auth.dto.RegisterRequest;
import com.security.auth.entity.User;
import com.security.auth.repository.UserRepository;
import com.security.auth.service.JwtService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Tambahkan ini

    public AuthController(JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        // Verifikasi password menggunakan matches
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Password salah!"));
        }

        List<String> roles = Arrays.asList(user.getRoles().split(","));
        String token = jwtService.generateToken(user.getUsername(), roles);
        
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // 1. Cek apakah username sudah dipakai
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username sudah terdaftar!"));
        }

        // 2. Buat objek User baru
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        
        // 3. ENKRIPSI PASSWORD sebelum simpan
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // 4. Set roles (default ke ROLE_USER jika kosong)
        String roles = (request.getRoles() == null) ? "ROLE_USER" : request.getRoles();
        newUser.setRoles(roles);

        // 5. Simpan ke database
        userRepository.save(newUser);

        return ResponseEntity.ok(Map.of("message", "User berhasil didaftarkan!"));
    }

}
