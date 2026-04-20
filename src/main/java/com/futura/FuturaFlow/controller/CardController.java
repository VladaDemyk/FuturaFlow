package com.futura.FuturaFlow.controller;

import com.futura.FuturaFlow.dto.CardDTO;
import com.futura.FuturaFlow.repository.CardRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "*") // Щоб Апідог не сварився на CORS
public class CardController {

    private final CardRepository cardRepository;

    public CardController(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    // PJM-121: Отримання списку карт
    @GetMapping
    public List<CardDTO> getAllCards() {
        return cardRepository.findAll()
                .stream()
                .map(CardDTO::new) // Тут працює маскування з Кроку 3
                .collect(Collectors.toList());
    }

    // PJM-123: Видалення карти
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCard(@PathVariable Long id) {
        if (cardRepository.existsById(id)) {
            cardRepository.deleteById(id);
            return ResponseEntity.ok("Карту успішно видалено");
        } else {
            return ResponseEntity.status(404).body("Карту з ID " + id + " не знайдено");
        }
    }
}