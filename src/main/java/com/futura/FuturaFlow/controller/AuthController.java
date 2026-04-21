package com.futura.FuturaFlow.controller;

import com.futura.FuturaFlow.dto.LoginRequestDTO;
import com.futura.FuturaFlow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // --- 1. МЕТОД ДЛЯ ЛОГІНУ ---
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        boolean isAuthenticated = authService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        if (isAuthenticated) {
            return ResponseEntity.ok("Вхід успішний! Вітаємо в системі.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Помилка: Невірний email або пароль.");
        }
    }

    // --- 2. МЕТОД ДЛЯ РЕЄСТРАЦІЇ ---
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody LoginRequestDTO registerRequest) {
        authService.registerUser(registerRequest.getEmail(), registerRequest.getPassword());
        return ResponseEntity.ok("Реєстрація успішна! Тепер ви можете увійти.");
    }
}