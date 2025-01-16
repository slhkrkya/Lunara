package com.example.Lunara.notification_service.controller;


import com.example.Lunara.notification_service.notification.Notification;
import com.example.Lunara.notification_service.respository.NotificationRespository;
import com.example.Lunara.notification_service.respository.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationRespository notificationRespository;

    public NotificationController(NotificationService notificationService, NotificationRespository notificationRespository) {
        this.notificationService = notificationService;
        this.notificationRespository = notificationRespository;
    }
    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationService.createNotification(notification.getUserId(), notification.getMessage());
    }

    @GetMapping("/{userId}")
    public List<Notification> getNotificationsByUserId(@PathVariable Long userId) {
        return notificationService.getNotificationsByUserId(userId);
    }
    @PutMapping("/markAsRead/{id}")
    public void markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id); // Bildirimi okundu olarak işaretle ve sil
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bildirim bulunamadı", e);
        }
    }
}
