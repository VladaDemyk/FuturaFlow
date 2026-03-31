package com.futura.FuturaFlow.service;

import com.futura.FuturaFlow.entity.Invoice;
import com.futura.FuturaFlow.repository.InvoiceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class FileCleanupService {

    private final InvoiceRepository invoiceRepository;
    private final String uploadDir = "uploads/";

    public FileCleanupService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    // Для тестування поставимо запуск кожну 1 хвилину: @Scheduled(fixedDelay = 60000)
    // У реальному житті це буде щоночі о 03:00: @Scheduled(cron = "0 0 3 * * ?")
    @Scheduled(fixedDelay = 60000)
    public void cleanUpOrphanFiles() {
        System.out.println("🧹 [Прибиральник] Запуск перевірки файлів...");

        // 1. Отримуємо всі інвойси з бази даних і витягуємо тільки назви файлів
        List<String> dbFileNames = invoiceRepository.findAll()
                .stream()
                .map(invoice -> new File(invoice.getFilePath()).getName())
                .toList();

        // 2. Беремо всі файли, що фізично лежать у папці uploads
        File folder = new File(uploadDir);
        File[] allFilesOnDisk = folder.listFiles();

        if (allFilesOnDisk == null) {
            return; // Якщо папки немає або вона порожня - виходимо
        }

        // Встановлюємо "вікно безпеки". Наприклад, видаляємо файли, старіші за 1 годину (3600000 мілісекунд)
        // Для тесту можеш зробити 1 хвилину (60000 мілісекунд)
        long safetyWindow = System.currentTimeMillis() - 60000;

        // 3. Порівнюємо і шукаємо "сміття"
        for (File file : allFilesOnDisk) {
            // Якщо файла немає в базі ДАНИХ
            if (!dbFileNames.contains(file.getName())) {
                // 4. Перевіряємо, чи пройшло "вікно безпеки"
                if (file.lastModified() < safetyWindow) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        System.out.println("🗑️ [Прибиральник] Видалено сироту: " + file.getName());
                    }
                }
            }
        }
    }
}
