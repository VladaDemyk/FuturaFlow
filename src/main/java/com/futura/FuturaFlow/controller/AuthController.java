package com.futura.FuturaFlow.controller;

<<<<<<< HEAD
import com.futura.FuturaFlow.entity.User;
import com.futura.FuturaFlow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
=======
import com.futura.FuturaFlow.dto.LoginRequestDTO;
import com.futura.FuturaFlow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
>>>>>>> 6f65f4d322f36ba58714c25edf2c0900a4bcf2d9
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

<<<<<<< HEAD
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
=======
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
>>>>>>> 6f65f4d322f36ba58714c25edf2c0900a4bcf2d9
    }
}