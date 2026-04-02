package com.futura.FuturaFlow.controller;

import com.futura.FuturaFlow.entity.User;
import com.futura.FuturaFlow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 1. Додали нашого "шифрувальника"

    // 2. Додали його сюди, щоб Spring Boot сам його нам дав
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        // 3. Беремо пароль, який ввела людина (наприклад, "12345")
        String rawPassword = user.getPassword();

        // 4. Перетворюємо його на складний хеш (наприклад, "{bcrypt}$2a$10$N9qo...")
        String hashedPassword = passwordEncoder.encode(rawPassword);

        // 5. Кладемо цей зашифрований пароль назад у нашого користувача
        user.setPassword(hashedPassword);

        // 6. Зберігаємо користувача в базу даних Aiven
        userRepository.save(user);

        return "Користувач " + user.getUsername() + " успішно приєднався до Futura (пароль зашифровано!)";
    }
}