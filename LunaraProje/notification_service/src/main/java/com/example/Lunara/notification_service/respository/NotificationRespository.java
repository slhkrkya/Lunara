package com.example.Lunara.notification_service.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Lunara.notification_service.notification.Notification;
import java.util.List;

public interface NotificationRespository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(long userId);
}
