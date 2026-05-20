package com.futura.FuturaFlow.service;

import com.futura.FuturaFlow.entity.Invoice;
import com.futura.FuturaFlow.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileCleanupServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @TempDir
    Path tempDir;

    private FileCleanupService fileCleanupService;

    @BeforeEach
    void setUp() throws Exception {
        fileCleanupService = new FileCleanupService(invoiceRepository);
        // Замінюємо uploadDir на тимчасову папку для тестів
        Field uploadDirField = FileCleanupService.class.getDeclaredField("uploadDir");
        uploadDirField.setAccessible(true);
        uploadDirField.set(fileCleanupService, tempDir.toString() + "/");
    }

    // --- Тест 1: Сирота-файл (якого немає в БД) видаляється ---
    @Test
    void cleanUpOrphanFiles_deletesOrphanFile() throws IOException {
        // Створюємо файл і робимо його "старим" (за межами вікна безпеки)
        Path orphanFile = tempDir.resolve("orphan_file.pdf");
        Files.writeString(orphanFile, "сирота");
        // Files.setLastModifiedTime() надійніший ніж File.setLastModified() на всіх ОС
        Files.setLastModifiedTime(orphanFile, FileTime.fromMillis(System.currentTimeMillis() - 120000));

        // БД порожня — жодного інвойсу
        when(invoiceRepository.findAll()).thenReturn(List.of());

        fileCleanupService.cleanUpOrphanFiles();

        assertFalse(Files.exists(orphanFile), "Файл-сирота повинен бути видалений");
    }

    // --- Тест 2: Файл із БД НЕ видаляється ---
    @Test
    void cleanUpOrphanFiles_doesNotDeleteFileRegisteredInDb() throws IOException {
        Path invoiceFile = tempDir.resolve("valid_invoice.pdf");
        Files.writeString(invoiceFile, "валідний інвойс");
        invoiceFile.toFile().setLastModified(System.currentTimeMillis() - 120000);

        // Створюємо інвойс у БД з цим файлом
        Invoice invoice = new Invoice();
        invoice.setFilePath(invoiceFile.toString());
        when(invoiceRepository.findAll()).thenReturn(List.of(invoice));

        fileCleanupService.cleanUpOrphanFiles();

        assertTrue(Files.exists(invoiceFile), "Файл зареєстрований у БД не повинен бути видалений");
    }

    // --- Тест 3: Свіжий файл (у межах вікна безпеки) НЕ видаляється ---
    @Test
    void cleanUpOrphanFiles_doesNotDeleteRecentOrphanFile() throws IOException {
        Path recentFile = tempDir.resolve("recent_orphan.pdf");
        Files.writeString(recentFile, "свіжий файл");
        // НЕ змінюємо lastModified — файл щойно створений (в межах вікна безпеки)

        when(invoiceRepository.findAll()).thenReturn(List.of());

        fileCleanupService.cleanUpOrphanFiles();

        assertTrue(Files.exists(recentFile), "Свіжий файл не повинен бути видалений навіть якщо він сирота");
    }

    // --- Тест 4: Якщо папка порожня — нічого не відбувається ---
    @Test
    void cleanUpOrphanFiles_doesNothingWhenFolderIsEmpty() {
        when(invoiceRepository.findAll()).thenReturn(List.of());

        // Очікуємо що жодного виклику репозиторію крім findAll не буде
        assertDoesNotThrow(() -> fileCleanupService.cleanUpOrphanFiles());
    }
}
