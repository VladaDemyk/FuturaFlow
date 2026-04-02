package service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class StorageService {

    // Вказуємо шлях до папки 'uploads' у директорії твого проєкту
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    public String saveFile(MultipartFile file) throws IOException {

        // 1. Перевіряємо, чи існує папка uploads. Якщо ні — створюємо її
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 2. Дістаємо розширення файлу (наприклад, .jpg або .pdf)
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 3. Генеруємо унікальне ім'я файлу за допомогою UUID
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // 4. Зберігаємо файл на диск
        File dest = new File(UPLOAD_DIR + uniqueFileName);
        file.transferTo(dest);

        // 5. Повертаємо відносний шлях, за яким файл можна буде знайти
        return "/uploads/" + uniqueFileName;
    }
}