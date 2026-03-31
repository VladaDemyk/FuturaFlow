package com.futura.FuturaFlow.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    // Папка у твоєму проєкті, куди будуть зберігатися всі чеки та інвойси
    private final String uploadDir = "uploads/";

    public String saveFile(MultipartFile file) throws IOException {
        // 1. Створюємо папку uploads, якщо її ще не існує
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 2. Генеруємо унікальне ім'я файлу (щоб чеки з однаковими назвами не перезаписували один одного)
        // Наприклад: "f47ac10b-58cc-4372-a567-0e02b2c3d479_check.pdf"
        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(uniqueFileName);

        // 3. Зберігаємо файл на комп'ютер (копіюємо потік даних у файл)
        Files.copy(file.getInputStream(), filePath);

        // 4. Повертаємо шлях до файлу (згодом ми будемо зберігати цей рядок у базу даних)
        return filePath.toString();
    }
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            // Перевіряє, чи є файл, і якщо є — видаляє його з жорсткого диска
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.out.println("Не вдалося видалити фізичний файл: " + e.getMessage());
        }
    }
}