package com.futura.FuturaFlow.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.futura.FuturaFlow.entity.User;
import com.futura.FuturaFlow.repository.UserRepository;

import java.util.Optional;

// Анотація @Service каже Spring, що тут живе бізнес-логіка
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // <-- ОСЬ ЦЬОГО РЯДКА НЕ ВИСТАЧАЛО

    // Конструктор: Spring сам підставить сюди потрібний репозиторій та шифрувальник
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder; // <-- І ОСЬ ЦЬОГО НЕ ВИСТАЧАЛО
    }

    // Головний метод перевірки пароля
    public boolean authenticate(String email, String rawPassword) {
        // 1. Шукаємо користувача в базі за email
        Optional<User> userOptional = userRepository.findByEmail(email);

        // 2. Якщо користувач знайдений
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 3. Порівнюємо паролі
            // (УВАГА: Оскільки ми тепер шифруємо паролі при реєстрації, тут теж треба використовувати passwordEncoder.matches)
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return true; // Пароль підійшов!
            }
        }

        // Якщо користувача немає або пароль не підійшов
        return false;
    }

    public void registerUser(String email, String password) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));

        // Використовуємо точні назви з твого класу User.java
        newUser.setUsername("Vlado");          // метод setUsername замість setName
        newUser.setLastName("Student");        // метод setLastName
        newUser.setPhone("+38" + System.currentTimeMillis());     // метод setPhone
        newUser.setDateOfBirth("2000-01-01");  // твоє поле String, тому пишемо текст

        userRepository.save(newUser);
    }
}