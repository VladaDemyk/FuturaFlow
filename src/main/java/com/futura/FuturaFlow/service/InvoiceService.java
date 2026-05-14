package com.futura.FuturaFlow.service;

import com.futura.FuturaFlow.entity.Invoice;
import com.futura.FuturaFlow.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    // Метод для викупу інвойсу
    public String buyOutInvoice(Long id) {
        // 1. Шукаємо інвойс у базі даних за його ID
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Інвойс із таким ID не знайдено"));

        // 2. ПЕРЕВІРКА ДОСТУПНОСТІ (наша головна задача 5 спринту)
        if (!"AVAILABLE".equals(invoice.getStatus())) {
            return "Помилка: Цей інвойс уже викуплений або недоступний!";
        }

        // 3. Якщо перевірка пройдена успішно — змінюємо статус
        invoice.setStatus("BOUGHT_OUT");
        invoiceRepository.save(invoice); // Зберігаємо оновлений статус у базу

        return "Успіх: Інвойс успішно викуплено!";
    }
}