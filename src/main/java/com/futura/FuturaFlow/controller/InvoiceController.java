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
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("percentage") Double percentage) {
        try {
            // 1. Зберігаємо фізичний файл на комп'ютер і отримуємо шлях до нього
            String savedFilePath = fileStorageService.saveFile(file);

            // 2. Створюємо новий запис для бази даних
            Invoice newInvoice = new Invoice();
            newInvoice.setInvoiceNumber(invoiceNumber);
            newInvoice.setAmount(amount);
            newInvoice.setFilePath(savedFilePath);
            newInvoice.setPercentage(percentage);
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
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInvoice(@PathVariable Long id) {
        // 1. Спочатку шукаємо, чи взагалі є такий інвойс у базі даних
        return invoiceRepository.findById(id).map(invoice -> {

            // 2. Якщо знайшли -> видаляємо його фізичний файл з папки uploads
            fileStorageService.deleteFile(invoice.getFilePath());

            // 3. Видаляємо сам рядок із таблиці MySQL
            invoiceRepository.deleteById(id);

            // 4. Кажемо клієнту, що все супер
            return ResponseEntity.ok("Інвойс успішно видалено!");

        }).orElse(ResponseEntity.status(404).body("Помилка: Інвойс з таким ID не знайдено"));
    }
    @PutMapping("/{id}/file")
    public ResponseEntity<String> updateInvoiceFile(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        return invoiceRepository.findById(id).map(invoice -> {
            try {
                // 1. Видаляємо старий фізичний файл (фотку кота)
                fileStorageService.deleteFile(invoice.getFilePath());

                // 2. Зберігаємо новий файл (правильний чек)
                String newFilePath = fileStorageService.saveFile(file);

                // 3. Оновлюємо шлях до файлу в нашому "зошиті" (базі даних)
                invoice.setFilePath(newFilePath);
                invoiceRepository.save(invoice);

                return ResponseEntity.ok("Файл успішно оновлено! Новий шлях: " + newFilePath);

            } catch (Exception e) {
                return ResponseEntity.status(500).body("Помилка при оновленні файлу: " + e.getMessage());
            }
        }).orElse(ResponseEntity.status(404).body("Помилка: Інвойс з таким ID не знайдено"));
    }
    @GetMapping("/filter")
    public ResponseEntity<List<Invoice>> filterInvoices(
            @RequestParam("minAmount") BigDecimal minAmount,
            @RequestParam("maxAmount") BigDecimal maxAmount) {

        // Викликаємо наш магічний метод з репозиторію
        List<Invoice> filteredInvoices = invoiceRepository.findByAmountBetween(minAmount, maxAmount);

        return ResponseEntity.ok(filteredInvoices);
    }
    @GetMapping("/filter/percentage")
    public ResponseEntity<List<Invoice>> filterByPercentage(
            @RequestParam("minPercent") Double minPercent,
            @RequestParam("maxPercent") Double maxPercent) {

        List<Invoice> filteredInvoices = invoiceRepository.findByPercentageBetween(minPercent, maxPercent);
        return ResponseEntity.ok(filteredInvoices);
    }
}
