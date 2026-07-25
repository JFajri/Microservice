package com.order.order.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
        LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Untuk kasus: produk/pelanggan tidak ditemukan -> 404
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleNotFound(IllegalArgumentException e) {
        log.info("Menangani IllegalArgumentException: Mengembalikan status 404 NOT_FOUND. Pesan: {}", e.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // Untuk kasus: service lain down / error umum lainnya -> 502
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntime(RuntimeException e) {
        log.info("Menangani RuntimeException: Mengembalikan status 502 BAD_GATEWAY. Pesan: {}", e.getMessage());
        return buildResponse(HttpStatus.BAD_GATEWAY, e.getMessage());
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
        log.info("Menyusun payload HTTP response body untuk status: {} ({})", status.value(), status.getReasonPhrase());
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}