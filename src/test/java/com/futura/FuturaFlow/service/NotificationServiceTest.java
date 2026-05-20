package com.futura.FuturaFlow.service;

import com.futura.FuturaFlow.entity.Notification;
import com.futura.FuturaFlow.entity.User;
import com.futura.FuturaFlow.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    // SimpMessagingTemplate не мокується Mockito на Java 24+ через обмеження модульної системи.
    // Використовуємо ручний фейк-клас що розширює реальний SimpMessagingTemplate.
    private FakeMessagingTemplate messagingTemplate;
    private NotificationService notificationService;

    private User testUser;

    /**
     * Фейковий MessagingTemplate — замість мока просто записує виклики у поля.
     * Не потребує Mockito і сумісний з будь-якою JVM версією.
     */
    static class FakeMessagingTemplate extends SimpMessagingTemplate {

        String lastUser;
        String lastDestination;
        Object lastPayload;

        // SimpMessagingTemplate потребує MessageChannel — передаємо порожню реалізацію
        // MessageChannel має два параметри: send(Message<?> message, long timeout)
        FakeMessagingTemplate() {
            super((message, timeout) -> true);
        }

        @Override
        public void convertAndSendToUser(String user, String destination, Object payload) {
            this.lastUser = user;
            this.lastDestination = destination;
            this.lastPayload = payload;
        }
    }

    @BeforeEach
    void setUp() {
        messagingTemplate = new FakeMessagingTemplate();
        notificationService = new NotificationService(notificationRepository, messagingTemplate);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@futura.com");
        testUser.setUsername("Іван");
    }

    // --- Тест 1: sendNotification зберігає сповіщення в базу ---
    @Test
    void sendNotification_savesNotificationToRepository() {
        notificationService.sendNotification(testUser, "Ваш інвойс викуплено!");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();
        assertEquals("Ваш інвойс викуплено!", saved.getMessage());
        assertEquals(testUser, saved.getUser());
        assertFalse(saved.isRead(), "Нове сповіщення повинно бути непрочитаним");
        assertNotNull(saved.getCreatedAt(), "Час створення повинен бути встановлений");
    }

    // --- Тест 2: sendNotification відправляє через WebSocket правильному користувачу ---
    @Test
    void sendNotification_sendsMessageToCorrectUserViaWebSocket() {
        notificationService.sendNotification(testUser, "Тестове повідомлення");

        assertEquals("user@futura.com", messagingTemplate.lastUser,
                "Повідомлення повинно піти на email юзера");
        assertEquals("/queue/notifications", messagingTemplate.lastDestination,
                "Destination повинен бути /queue/notifications");
        assertNotNull(messagingTemplate.lastPayload, "Payload не повинен бути null");
    }

    // --- Тест 3: getUserNotifications повертає список зі сховища ---
    @Test
    void getUserNotifications_returnsNotificationsFromRepository() {
        Notification n1 = new Notification();
        n1.setMessage("Перше");
        Notification n2 = new Notification();
        n2.setMessage("Друге");

        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(n1, n2));

        List<Notification> result = notificationService.getUserNotifications(1L);

        assertEquals(2, result.size());
        assertEquals("Перше", result.get(0).getMessage());
    }

    // --- Тест 4: markAsRead змінює статус на прочитано та зберігає ---
    @Test
    void markAsRead_setsReadTrueAndSaves() {
        Notification notification = new Notification();
        notification.setRead(false);

        when(notificationRepository.findById(10L)).thenReturn(Optional.of(notification));

        notificationService.markAsRead(10L);

        assertTrue(notification.isRead(), "Сповіщення повинно стати прочитаним");
        verify(notificationRepository).save(notification);
    }

    // --- Тест 5: markAsRead нічого не робить якщо сповіщення не знайдено ---
    @Test
    void markAsRead_doesNothingWhenNotificationNotFound() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        notificationService.markAsRead(999L);

        verify(notificationRepository, never()).save(any());
    }
}
