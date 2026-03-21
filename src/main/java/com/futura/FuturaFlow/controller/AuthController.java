package com.futura.FuturaFlow.controller;
import com.futura.FuturaFlow.dto.LoginRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        System.out.println("Спроба входу для email: " + loginRequest.getEmail());

        return ResponseEntity.ok("Формат правильний. Сервер отримав дані!");
    }
}
