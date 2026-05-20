package com.futura.FuturaFlow.service;

import com.futura.FuturaFlow.entity.Invoice;
import com.futura.FuturaFlow.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    private Invoice availableInvoice;

    @BeforeEach
    void setUp() {
        availableInvoice = new Invoice();
        availableInvoice.setInvoiceNumber("INV-001");
        availableInvoice.setAmount(new BigDecimal("5000.00"));
        availableInvoice.setStatus("AVAILABLE");
    }

    // --- Тест 1: Успішний викуп інвойсу ---
    @Test
    void buyOutInvoice_success_whenStatusIsAvailable() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(availableInvoice));

        String result = invoiceService.buyOutInvoice(1L);

        assertEquals("Успіх: Інвойс успішно викуплено!", result);
        // Перевіряємо що статус дійсно змінився
        assertEquals("BOUGHT_OUT", availableInvoice.getStatus());
        // Перевіряємо що інвойс зберігся в БД
        verify(invoiceRepository).save(availableInvoice);
    }

    // --- Тест 2: Інвойс вже викуплений — має повернути помилку ---
    @Test
    void buyOutInvoice_returnsErrorMessage_whenInvoiceAlreadyBoughtOut() {
        availableInvoice.setStatus("BOUGHT_OUT");
        when(invoiceRepository.findById(2L)).thenReturn(Optional.of(availableInvoice));

        String result = invoiceService.buyOutInvoice(2L);

        assertEquals("Помилка: Цей інвойс уже викуплений або недоступний!", result);
        // Перевіряємо що save() НЕ викликався (не змінили нічого)
        verify(invoiceRepository, never()).save(any());
    }

    // --- Тест 3: Інвойса немає в базі — має кинути виняток ---
    @Test
    void buyOutInvoice_throwsRuntimeException_whenInvoiceNotFound() {
        when(invoiceRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> invoiceService.buyOutInvoice(999L),
                "Повинен бути кинутий RuntimeException"
        );

        assertTrue(exception.getMessage().contains("Інвойс із таким ID не знайдено"));
    }

    // --- Тест 4: Після успішного викупу — статус більше не AVAILABLE ---
    @Test
    void buyOutInvoice_changesStatusFromAvailableToBoughtOut() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(availableInvoice));

        assertEquals("AVAILABLE", availableInvoice.getStatus()); // до
        invoiceService.buyOutInvoice(1L);
        assertEquals("BOUGHT_OUT", availableInvoice.getStatus()); // після
    }
}
