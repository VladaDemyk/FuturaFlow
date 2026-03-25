package com.futura.FuturaFlow.AuthController;

import com.futura.FuturaFlow.entity.User;
import com.futura.FuturaFlow.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    // Підключаємо нашого "помічника"
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Створюємо команду для реєстрації
    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        userRepository.save(user); // Ось ця команда зберігає людину в базу!
        return "Користувач " + user.getUsername() + " успішно приєднався до Futura!";
    }
}