package com.futura.FuturaFlow.service;

import com.futura.FuturaFlow.entity.User;
import com.futura.FuturaFlow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Анотація @Service каже Spring, що тут живе бізнес-логіка
@Service
public class AuthService {

    private final UserRepository userRepository;

    // Конструктор: Spring сам підставить сюди потрібний репозиторій (Dependency Injection)
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Головний метод перевірки пароля
    public boolean authenticate(String email, String rawPassword) {
        // 1. Шукаємо користувача в базі за email
        Optional<User> userOptional = userRepository.findByEmail(email);

        // 2. Якщо користувач знайдений
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 3. Порівнюємо паролі (Поки що як звичайний текст. Хешування BCrypt додамо згодом)
            if (user.getPassword().equals(rawPassword)) {
                return true; // Пароль підійшов!
            }
        }

        // Якщо користувача немає або пароль не підійшов
        return false;
    }
}
