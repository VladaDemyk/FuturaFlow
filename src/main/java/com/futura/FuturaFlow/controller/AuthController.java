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

    // Spring автоматично знайде наш AuthService і передасть його сюди
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginRequestDTO loginRequest) {

        // 1. Передаємо дані з DTO в наш сервіс для перевірки в базі даних
        boolean isAuthenticated = authService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        // 2. Формуємо правильну HTTP-відповідь на основі результату
        if (isAuthenticated) {
            // Статус 200 OK
            return ResponseEntity.ok("Вхід успішний! Вітаємо в системі.");
        } else {
            // Статус 401 Unauthorized (Неавторизовано)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Помилка: Невірний email або пароль.");
        }
    }
}