package com.security.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.security.auth.service.JwtService;


import java.io.IOException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private static final Logger log =
        LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("JWT Authentication dilewati: Header Authorization tidak ditemukan atau tidak valid");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);
        log.info("Memproses autentikasi untuk username: {}", username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("Memvalidasi token JWT untuk username: {}", username);
            if (jwtService.isTokenValid(jwt)) {
                var roles = jwtService.extractRoles(jwt);
                log.info("Token valid. Mengkstrak roles: {}", roles);
                
                var authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, null, authorities
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("Autentikasi berhasil diset ke SecurityContext untuk username: {}", username);
            } else {
                log.info("Token JWT tidak valid untuk username: {}", username);
            }
        }
        filterChain.doFilter(request, response);
    }
}
