package com.futura.FuturaFlow.controller;

import com.futura.FuturaFlow.entity.User;
import com.futura.FuturaFlow.repository.UserRepository;
import com.futura.FuturaFlow.dto.LoginRequestDTO;
import com.futura.FuturaFlow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    // Об'єднуємо всі залежності в один конструктор
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    // --- ЛОГІКА РЕЄСТРАЦІЇ ---
    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        String rawPassword = user.getPassword();
        String hashedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(hashedPassword);
        userRepository.save(user);

        return "Користувач " + user.getUsername() + " успішно приєднався до Futura (пароль зашифровано!)";
    }

    // --- ЛОГІКА ВХОДУ (ЛОГІН) ---
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
}