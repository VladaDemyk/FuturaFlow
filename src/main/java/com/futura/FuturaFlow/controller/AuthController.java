package com.futura.FuturaFlow.controller;

import com.futura.FuturaFlow.dto.AuthResponse;
import com.futura.FuturaFlow.dto.GoogleLoginRequest;
import com.futura.FuturaFlow.dto.LoginRequestDTO;
import com.futura.FuturaFlow.entity.User;
import com.futura.FuturaFlow.repository.UserRepository;
import com.futura.FuturaFlow.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Щоб фронтенд не сварився на CORS
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    // --- НАЛАШТУВАННЯ ДЛЯ JWT ---
    // Генеруємо секретний ключ для підпису токенів (в майбутньому винесемо в application.yaml)
    private final Key jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long JWT_EXPIRATION_MS = 86400000; // Токен живе 1 день (в мілісекундах)

    // Об'єднуємо всі залежності в один конструктор
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    // --- ЛОГІКА РЕЄСТРАЦІЇ (Стара) ---
    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        String rawPassword = user.getPassword();
        String hashedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(hashedPassword);
        userRepository.save(user);

        return "Користувач " + user.getUsername() + " успішно приєднався до Futura (пароль зашифровано!)";
    }

    // --- ЛОГІКА ВХОДУ (Стара) ---
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

    // --- НОВА ЛОГІКА: ВХІД ЧЕРЕЗ GOOGLE ---
    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody GoogleLoginRequest request) {
        String googleToken = request.getToken();

        // 1. Валідація токена від фронту
        // (Поки що це заглушка, поки Макс або фронтенд не дасть реальний Client ID)
        String userEmail = "google.test@futura.com"; // Імітуємо email, дістатий з токена

        // 2. Шукаємо юзера в базі
        Optional<User> existingUser = userRepository.findByEmail(userEmail);
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get(); // Якщо є - беремо його
        } else {
            // Якщо немає - створюємо нового
            user = new User();
            user.setEmail(userEmail);
            user.setUsername(userEmail); // Ставимо email як username, щоб не було помилок БД
            // Пароль не ставимо, бо це логін через Google
            userRepository.save(user);
        }

        // 3. Генеруємо наш власний JWT токен
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);

        String jwt = Jwts.builder()
                .setSubject(user.getEmail()) // Зашиваємо email у токен
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey) // Підписуємо секретним ключем
                .compact();

        // Віддаємо токен клієнту (фронтенду)
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
    // --- НОВА ЛОГІКА: MOCK BANKID (Заглушка) ---
    @PostMapping("/bankid-mock")
    public ResponseEntity<?> mockBankIdLogin() {
        // 1. Імітуємо дані ФОПа, які нібито прийшли від BankID
        Map<String, String> fopUser = new HashMap<>();
        fopUser.put("fullName", "Дмитренко В.О.");
        fopUser.put("inn", "1234567890");
        fopUser.put("role", "FOP");

        // 2. Генеруємо справжній JWT токен (використовуємо твій існуючий ключ jwtSecretKey)
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);

        String jwt = Jwts.builder()
                .setSubject(fopUser.get("inn")) // Зашиваємо ІПН як ідентифікатор
                .claim("role", fopUser.get("role")) // Додаємо роль ФОПа
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey)
                .compact();

        // 3. Формуємо відповідь для фронтенду
        Map<String, Object> response = new HashMap<>();
        response.put("message", "BankID верифікація успішна");
        response.put("user", fopUser);
        response.put("token", jwt);

        return ResponseEntity.ok(response);
    }
}