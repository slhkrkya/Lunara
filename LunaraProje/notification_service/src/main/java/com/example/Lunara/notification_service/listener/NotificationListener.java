package com.example.Lunara.notification_service.listener;

import com.example.Lunara.notification_service.DTO.NotificationRequest;
import com.example.Lunara.notification_service.respository.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationListener {
    private final NotificationService notificationService;
    public NotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    @RabbitListener(queues = "notificationQueue")
    public void handleNotification(NotificationRequest notificationRequest) {
        System.out.println("RabbitMQ'dan alınan mesaj: " + notificationRequest);
        notificationService.createNotification(notificationRequest.getUserId(), notificationRequest.getMessage());
    }
}
