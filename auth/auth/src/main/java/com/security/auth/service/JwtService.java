package com.security.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JwtService {

    // Gunakan key yang kuat. Minimal 256-bit untuk HS256
    private static final String SECRET_KEY = "kunci_rahasia_microservice_yang_sangat_panjang_dan_aman_123";

    private static final Logger log =
        LoggerFactory.getLogger(JwtService.class);

    public String extractUsername(String token) {
        log.info("Mengambil username dari token");
        return extractClaim(token, Claims::getSubject);
    }

    public List<String> extractRoles(String token) {
        log.info("Mengambil roles dari token");
        final Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        log.info("Mengambil claim spesifik dari token");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(String username, List<String> roles) {
        log.info("Membuat token baru untuk user: {} dengan roles: {}", username, roles);
        return Jwts.builder()
                .setClaims(Map.of("roles", roles))
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 jam
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        log.info("Memvalidasi masa berlaku token");
        return !extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        log.info("Membaca seluruh claims dari token");
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        log.info("Mengambil kunci tanda tangan (signing key)");
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

}
