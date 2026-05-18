package com.security.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

// 1. Samakan key-nya dengan yang ada di application.properties (jwt.secret)
    @Value("${jwt.secret}")
    private String secretKey;

    // 2. Samakan key-nya dengan yang ada di application.properties (jwt.expiration)
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<String> extractRoles(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(String username, List<String> roles) {
            return Jwts.builder()
                    .setClaims(Map.of("roles", roles))
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    // 3. PASTIKAN bagian ini menggunakan variabel jwtExpiration, BUKAN angka perkalian manual lagi
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) 
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        }

    // PERBAIKAN UTAMA: Menggunakan try-catch untuk menangani token expired
    public boolean isTokenValid(String token) {
        try {
            // Jika token bisa di-parse tanpa error, dan belum melewati waktu expired, kembalikan true
            return !extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (ExpiredJwtException e) {
            // Jika token terdeteksi expired, tangkap exceptionnya dan kembalikan false secara aman
            System.out.println("Token telah kedaluwarsa: " + e.getMessage());
            return false;
        } catch (Exception e) {
            // Jika token tidak valid karena alasan lain (salah kunci, dimodifikasi, dll)
            System.out.println("Token tidak valid: " + e.getMessage());
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        // Jika secretKey kamu berbentuk string biasa (bukan Base64), gunakan getBytes() seperti bawaanmu
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}