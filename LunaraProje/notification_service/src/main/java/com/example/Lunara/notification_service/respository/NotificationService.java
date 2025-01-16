package com.example.Lunara.notification_service.respository;

import com.example.Lunara.notification_service.notification.Notification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRespository notificationRespository;

    public NotificationService(NotificationRespository notificationRespository) {
        this.notificationRespository = notificationRespository;
    }
    public Notification createNotification(Long userId, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        return notificationRespository.save(notification);
    }
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRespository.findByUserId(userId);
    }
    public void markAsRead(Long id) {
        Notification notification = notificationRespository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notificationRespository.delete(notification);
    }
}
