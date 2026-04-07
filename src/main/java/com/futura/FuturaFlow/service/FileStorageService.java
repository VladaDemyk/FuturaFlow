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

    public String[] saveFileAndThumbnail(MultipartFile file) throws IOException {
        // 1. Створюємо папку (логіка як у Максима)
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 2. Формуємо унікальні імена для обох файлів
        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path originalFilePath = uploadPath.resolve(uniqueFileName);
        Path thumbFilePath = uploadPath.resolve("thumb_" + uniqueFileName);

        // 3. Зберігаємо ОРИГІНАЛ
        Files.copy(file.getInputStream(), originalFilePath);

        // 4. ГЕНЕРУЄМО ПРЕВ'Ю
        String contentType = file.getContentType();
        String finalThumbPath = null;

        if (contentType != null && contentType.startsWith("image/")) {
            // Стискаємо картинку
            net.coobird.thumbnailator.Thumbnails.of(originalFilePath.toFile())
                    .size(300, 300)
                    .outputQuality(0.8)
                    .toFile(thumbFilePath.toFile());

            finalThumbPath = thumbFilePath.toString();
        }

        // 5. Повертаємо масив з двох шляхів: [0] - оригінал, [1] - прев'ю
        return new String[]{originalFilePath.toString(), finalThumbPath};
    }
}