package com.futura.FuturaFlow.service;

import com.futura.FuturaFlow.entity.Notification;
import com.futura.FuturaFlow.entity.User;
import com.futura.FuturaFlow.repository.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate; // Об'єкт для відправки через WebSocket

    public NotificationService(NotificationRepository notificationRepository, SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // Головний метод: створює сповіщення, зберігає і відправляє
    public void sendNotification(User user, String messageText) {
        // 1. Створюємо об'єкт сповіщення
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(messageText);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        // 2. Зберігаємо в базу даних (для "дзвіночка")
        notificationRepository.save(notification);

        // 3. Відправляємо через WebSocket конкретному користувачу
        // Фронтенд буде слухати канал: /user/{username}/queue/notifications
        messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/queue/notifications",
                notification
        );
    }

    // Отримати історію сповіщень для юзера
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Позначити сповіщення як прочитане
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}