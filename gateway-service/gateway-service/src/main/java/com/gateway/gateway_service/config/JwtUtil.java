package com.gateway.gateway_service.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;

@Component
public class JwtUtil {
    // HARUS SAMA DENGAN AUTH-SERVICE
    public static final String SECRET = "kunci_rahasia_microservice_yang_sangat_panjang_dan_aman_123";  //diambil dari aut-service: jwtservice

    public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }
}
