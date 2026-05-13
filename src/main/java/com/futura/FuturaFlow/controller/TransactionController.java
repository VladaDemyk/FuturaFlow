package com.futura.FuturaFlow.controller;


import com.futura.FuturaFlow.entity.Transaction;
import com.futura.FuturaFlow.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // ТАСКА 1: Summary (Загальний баланс і сума виплат)
    @GetMapping("/summary")
    public Map<String, BigDecimal> getSummary() {
        BigDecimal totalProfit = transactionRepository.sumAmountByType("profit");
        BigDecimal totalPayout = transactionRepository.sumAmountByType("payout");

        // Якщо в базі порожньо, ставимо нулі, щоб не було помилок
        if (totalProfit == null) totalProfit = BigDecimal.ZERO;
        if (totalPayout == null) totalPayout = BigDecimal.ZERO;

        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("totalBalance", totalProfit.subtract(totalPayout));
        summary.put("totalPayouts", totalPayout);

        return summary;
    }

    // ТАСКА 2: History (Пагінація, фільтрація, сортування)
    @GetMapping("/history")
    public Page<Transaction> getHistory(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page, // offset (сторінка)
            @RequestParam(defaultValue = "10") int size // limit (кількість)
    ) {
        // Сортування за датою за спаданням (найновіші зверху)
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        // Якщо передали тип (profit/payout) - фільтруємо, інакше віддаємо всі
        if (type != null && !type.isEmpty()) {
            return transactionRepository.findByType(type, pageable);
        }
        return transactionRepository.findAll(pageable);
    }
}