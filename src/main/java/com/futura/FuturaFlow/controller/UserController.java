package com.futura.FuturaFlow.controller;

import com.futura.FuturaFlow.dto.UserProfileDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    // ТАСКА 3: Отримання профілю (Protected)
    @GetMapping("/profile")
    public UserProfileDTO getUserProfile() {
        // У майбутньому тут буде витягування юзера з Principal (токена)
        // Поки що повертаємо заглушку, щоб фронтенд міг малювати інтерфейс
        return new UserProfileDTO(
                "Влада",
                "vlada.founder@futura.com",
                "https://ui-avatars.com/api/?name=Vlada&background=random"
        );
    }
}