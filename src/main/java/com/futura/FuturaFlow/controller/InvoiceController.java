package com.futura.FuturaFlow.controller;

import com.futura.FuturaFlow.entity.Invoice;
import com.futura.FuturaFlow.repository.InvoiceRepository;
import com.futura.FuturaFlow.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final FileStorageService fileStorageService;
    private final InvoiceRepository invoiceRepository;

    // Знову використовуємо Dependency Injection
    public InvoiceController(FileStorageService fileStorageService, InvoiceRepository invoiceRepository) {
        this.fileStorageService = fileStorageService;
        this.invoiceRepository = invoiceRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadInvoice(
            @RequestParam("file") MultipartFile file,
            @RequestParam("invoiceNumber") String invoiceNumber,
            @RequestParam("amount") BigDecimal amount) {

        try {
            // 1. Зберігаємо фізичний файл на комп'ютер і отримуємо шлях до нього
            String savedFilePath = fileStorageService.saveFile(file);

            // 2. Створюємо новий запис для бази даних
            Invoice newInvoice = new Invoice();
            newInvoice.setInvoiceNumber(invoiceNumber);
            newInvoice.setAmount(amount);
            newInvoice.setFilePath(savedFilePath);

            // 3. Зберігаємо інформацію в MySQL
            invoiceRepository.save(newInvoice);

            return ResponseEntity.ok("Інвойс успішно завантажено! Шлях: " + savedFilePath);

        } catch (Exception e) {
            // Якщо щось пішло не так (наприклад, немає доступу до папки)
            return ResponseEntity.status(500).body("Помилка збереження файлу: " + e.getMessage());
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        // База даних сама дістане всі рядки з таблиці invoices
        List<Invoice> invoices = invoiceRepository.findAll();

        // Повертаємо їх клієнту зі статусом 200 OK
        return ResponseEntity.ok(invoices);
    }
}
