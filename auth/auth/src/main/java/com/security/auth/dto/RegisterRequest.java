package com.security.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String roles; // Contoh: "ROLE_USER"
}