package com.futura.FuturaFlow.controller;

import com.futura.FuturaFlow.entity.Invoice;
import com.futura.FuturaFlow.service.InvoiceService; // 1. Імпортуємо наш новий сервіс
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService; // 2. Створюємо поле для сервісу

    // 3. Додаємо сервіс у конструктор (Dependency Injection)
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // ... твої попередні методи (upload, filter тощо) ...

    // 4. Новий ендпоінт для викупу
    @PostMapping("/{id}/buyout")
    public ResponseEntity<String> buyout(@PathVariable Long id) {
        String result = invoiceService.buyOutInvoice(id);

        // Якщо сервіс повернув рядок, що починається з "Помилка"
        if (result.startsWith("Помилка")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }

        return ResponseEntity.ok(result);
    }
}