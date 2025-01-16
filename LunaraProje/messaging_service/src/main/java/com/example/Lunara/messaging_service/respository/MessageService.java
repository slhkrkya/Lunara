package com.example.Lunara.messaging_service.respository;

import com.example.Lunara.messaging_service.DTO.User;
import com.example.Lunara.messaging_service.client.UserClient;
import com.example.Lunara.messaging_service.message.Message;
import com.example.Lunara.messaging_service.DTO.NotificationRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final MessageRespository messageRepository;
    private final UserClient userClient;
    private final RabbitTemplate rabbitTemplate;

    public MessageService(MessageRespository messageRepository, UserClient userClient, RabbitTemplate rabbitTemplate) {
        this.messageRepository = messageRepository;
        this.userClient = userClient;
        this.rabbitTemplate = rabbitTemplate;
    }
    public Message sendMessage(Message message) {
        // Gönderen kullanıcıyı doğrula
        User sender = userClient.getUserById(message.getSenderId());
        if (sender == null) {
            throw new RuntimeException("Gönderen kullanıcı bulunamadı: ID = " + message.getSenderId());
        }

        // Alıcı kullanıcıyı doğrula
        User receiver = userClient.getUserById(message.getReceiverId());
        if (receiver == null) {
            throw new RuntimeException("Alıcı kullanıcı bulunamadı: ID = " + message.getReceiverId());
        }

        // Mesajı kaydet
        message.setTimestamp(LocalDateTime.now());
        Message savedMessage = messageRepository.save(message);

        // Bildirim oluşturma ve RabbitMQ kuyruğuna gönderme
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setUserId((message.getReceiverId()));
        notificationRequest.setMessage("Yeni bir mesaj aldınız:"+message.getContent());
        rabbitTemplate.convertAndSend("notificationQueue",notificationRequest);
        logger.info("RabbitMQ'ya gönderilen mesaj: {}", notificationRequest);
        return savedMessage;
    }

    public List<Message> getMessagesByReceiverId(Long receiverId) {
        return messageRepository.findByReceiverId(receiverId);
    }

    public List<Message> getMessagesBySenderId(Long senderId) {
        return messageRepository.findBySenderId(senderId);
    }

    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setRead(true);
        messageRepository.save(message);
    }
}