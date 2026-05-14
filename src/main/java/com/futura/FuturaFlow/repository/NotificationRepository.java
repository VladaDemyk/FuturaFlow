package com.futura.FuturaFlow.repository;

import com.futura.FuturaFlow.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Знайти всі сповіщення конкретного юзера, відсортовані від найновіших
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Порахувати кількість непрочитаних сповіщень для "дзвіночка"
    long countByUserIdAndIsReadFalse(Long userId);
}