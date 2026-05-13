package com.security.auth;

import com.security.auth.entity.User;
import com.security.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

    @Bean
    CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                // Password dienkripsi sebelum simpan ke DB
                admin.setPassword(passwordEncoder.encode("admin123")); 
                admin.setRoles("ROLE_ADMIN,ROLE_USER");
                userRepository.save(admin);
                System.out.println(">>> User Admin (BCrypt) berhasil dibuat!");
            }
        };
    }
}