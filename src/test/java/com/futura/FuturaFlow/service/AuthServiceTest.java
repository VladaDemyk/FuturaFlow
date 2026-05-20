package com.futura.FuturaFlow.service;

import com.futura.FuturaFlow.entity.User;
import com.futura.FuturaFlow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@futura.com");
        testUser.setPassword("$2a$encodedPassword"); // хешований пароль
        testUser.setUsername("TestUser");
    }

    // --- Тест 1: Успішний вхід (email і пароль правильні) ---
    @Test
    void authenticate_returnsTrue_whenCredentialsAreValid() {
        when(userRepository.findByEmail("test@futura.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("rawPassword", "$2a$encodedPassword")).thenReturn(true);

        boolean result = authService.authenticate("test@futura.com", "rawPassword");

        assertTrue(result, "Автентифікація повинна повертати true при правильних даних");
    }

    // --- Тест 2: Невірний пароль ---
    @Test
    void authenticate_returnsFalse_whenPasswordIsWrong() {
        when(userRepository.findByEmail("test@futura.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "$2a$encodedPassword")).thenReturn(false);

        boolean result = authService.authenticate("test@futura.com", "wrongPassword");

        assertFalse(result, "Автентифікація повинна повертати false при невірному паролі");
    }

    // --- Тест 3: Користувач не існує в базі ---
    @Test
    void authenticate_returnsFalse_whenUserNotFound() {
        when(userRepository.findByEmail("nobody@futura.com")).thenReturn(Optional.empty());

        boolean result = authService.authenticate("nobody@futura.com", "anyPassword");

        assertFalse(result, "Автентифікація повинна повертати false коли юзера немає");
        // Перевіряємо що PasswordEncoder взагалі не викликався (оптимізація)
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    // --- Тест 4: Реєстрація зберігає користувача з ЗАШИФРОВАНИМ паролем ---
    @Test
    void registerUser_savesUserWithEncodedPassword() {
        when(passwordEncoder.encode("mySecret")).thenReturn("$2a$hashedSecret");

        authService.registerUser("new@futura.com", "mySecret");

        // Захоплюємо об'єкт User, який пішов у save()
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("new@futura.com", savedUser.getEmail());
        assertEquals("$2a$hashedSecret", savedUser.getPassword(), "Пароль повинен бути зашифрований");
        assertNotEquals("mySecret", savedUser.getPassword(), "Пароль не повинен зберігатись в чистому вигляді");
    }

    // --- Тест 5: Реєстрація завжди викликає save() ---
    @Test
    void registerUser_callsRepositorySaveOnce() {
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$hash");

        authService.registerUser("user@futura.com", "pass");

        verify(userRepository, times(1)).save(any(User.class));
    }
}
