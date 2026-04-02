package controller;

import dto.FileUploadResponse;
import service.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/files") // Базовий URL для цього контролера
public class FileController {

    private final StorageService storageService;

    // Список дозволених форматів згідно з таскою (PJM-99)
    private final List<String> ALLOWED_TYPES = List.of("image/jpeg", "application/pdf");

    // Spring автоматично підставить (інжектить) наш StorageService
    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload") // Повний URL буде: POST /api/v1/files/upload
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {

        // 1. Валідація: чи файл взагалі передали
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Помилка: Файл не вибрано або він порожній");
        }

        // 2. Валідація: перевірка формату (.jpg, .pdf)
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body("Помилка: Недопустимий формат файлу. Дозволені тільки .jpg та .pdf");
        }

        try {
            // 3. Передаємо файл у наш сервіс для збереження
            String fileUrl = storageService.saveFile(file);

            // 4. Якщо все добре, повертаємо наш DTO з посиланням на файл
            return ResponseEntity.ok(new FileUploadResponse(fileUrl, "Файл успішно завантажено!"));

        } catch (Exception e) {
            // 5. Обробка непередбачених помилок (наприклад, немає прав на запис на диск)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Помилка сервера під час збереження файлу: " + e.getMessage());
        }
    }
}