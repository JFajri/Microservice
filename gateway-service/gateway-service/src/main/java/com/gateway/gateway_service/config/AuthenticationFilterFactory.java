package com.gateway.gateway_service.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthenticationFilterFactory extends AbstractGatewayFilterFactory<AuthenticationFilterFactory.Config> {

    private final JwtUtil jwtUtil;

    public AuthenticationFilterFactory(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    public static class Config { }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // JIKA TIDAK ADA HEADER -> LANGSUNG TOLAK (401)
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Kunci (Token) tidak ditemukan!");
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
                try {
                    jwtUtil.validateToken(authHeader);
                } catch (Exception e) {
                    // JIKA TOKEN SALAH/EXPIRED -> TOLAK (401)
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token Palsu atau Kadaluarsa!");
                }
            }
            return chain.filter(exchange);
        };
    }

}
