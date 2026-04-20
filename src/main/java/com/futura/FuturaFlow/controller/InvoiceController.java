package com.futura.FuturaFlow.controller;

import com.futura.FuturaFlow.entity.Invoice;
import com.futura.FuturaFlow.repository.InvoiceRepository;
import com.futura.FuturaFlow.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
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
    @GetMapping("/active")
    public ResponseEntity<Page<Invoice>> getActiveRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Створюємо об'єкт з налаштуваннями пагінації
        Pageable paging = PageRequest.of(page, size);

        // Передаємо ДВА параметри: статус і налаштування сторінки
        Page<Invoice> activeInvoices = invoiceRepository.findByStatus("ACTIVE", paging);

        return ResponseEntity.ok(activeInvoices);
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
    @PostMapping("/{id}/redeem")
    public ResponseEntity<String> redeemInvoice(@PathVariable Long id) {

        // 1. Просимо базу знайти інвойс за його ID
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(id);

        // 2. Перевіряємо, чи взагалі існує такий інвойс
        if (optionalInvoice.isPresent()) {
            Invoice invoice = optionalInvoice.get();

            // (Опціонально) Перевіряємо, чи він часом вже не викуплений
            if ("REDEEMED".equals(invoice.getStatus())) {
                return ResponseEntity.badRequest().body("Помилка: Цей інвойс вже викуплено!");
            }

            // 3. Змінюємо статус на "Викуплено" (REDEEMED)
            invoice.setStatus("REDEEMED");
            // Якщо у вас в команді інше слово для статусу (наприклад, "PAID" або "BOUGHT"), зміни його тут

            // 4. Зберігаємо оновлений інвойс назад у базу
            invoiceRepository.save(invoice);

            return ResponseEntity.ok("Успіх! Статус інвойсу змінено на ВИКУПЛЕНО.");
        } else {
            // Якщо інвойса з таким ID немає в базі
            return ResponseEntity.status(404).body("Інвойс з ID " + id + " не знайдено.");
        }
    }
}
