package com.futura.FuturaFlow.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    private final FileStorageService fileStorageService = new FileStorageService();

    // --- Тест 1: saveFile зберігає файл і повертає шлях ---
    @Test
    void saveFile_createsFileAndReturnsPath() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-invoice.pdf",
                "application/pdf",
                "Тестовий вміст файлу".getBytes()
        );

        String savedPath = fileStorageService.saveFile(mockFile);

        assertNotNull(savedPath, "Шлях не повинен бути null");
        assertTrue(savedPath.endsWith("test-invoice.pdf"), "Шлях повинен містити оригінальну назву файлу");

        // Прибираємо за собою
        Files.deleteIfExists(Path.of(savedPath));
    }

    // --- Тест 2: saveFile генерує унікальне ім'я (UUID) для кожного файлу ---
    @Test
    void saveFile_generatesDifferentPathsForSameFileName() throws IOException {
        MockMultipartFile file1 = new MockMultipartFile("file", "invoice.pdf", "application/pdf", "вміст1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "invoice.pdf", "application/pdf", "вміст2".getBytes());

        String path1 = fileStorageService.saveFile(file1);
        String path2 = fileStorageService.saveFile(file2);

        assertNotEquals(path1, path2, "Два файли з однаковою назвою повинні отримати різні шляхи (UUID)");

        // Прибираємо за собою
        Files.deleteIfExists(Path.of(path1));
        Files.deleteIfExists(Path.of(path2));
    }

    // --- Тест 3: deleteFile видаляє існуючий файл ---
    @Test
    void deleteFile_removesExistingFile(@TempDir Path tempDir) throws IOException {
        // Створюємо тимчасовий файл
        Path testFile = tempDir.resolve("to-delete.pdf");
        Files.writeString(testFile, "вміст");
        assertTrue(Files.exists(testFile));

        fileStorageService.deleteFile(testFile.toString());

        assertFalse(Files.exists(testFile), "Файл повинен бути видалений");
    }

    // --- Тест 4: deleteFile не кидає виняток якщо файл не існує ---
    @Test
    void deleteFile_doesNotThrowWhenFileNotFound() {
        // Просто переконуємось що немає Exception
        assertDoesNotThrow(() ->
                fileStorageService.deleteFile("uploads/nonexistent-file-xyz.pdf")
        );
    }
}
