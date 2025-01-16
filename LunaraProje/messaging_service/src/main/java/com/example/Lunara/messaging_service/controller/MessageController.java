package com.example.Lunara.messaging_service.controller;

import com.example.Lunara.messaging_service.message.Message;
import com.example.Lunara.messaging_service.respository.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/message")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }
    // Mesaj gönderme
    @PostMapping
    public Message sendMessage(@RequestBody Message message) {
        return messageService.sendMessage(message);
    }

    // Alıcıya ait mesajları alma
    @GetMapping("/receiver/{receiverId}")
    public List<Message> getMessagesByReceiverId(@PathVariable Long receiverId) {
        return messageService.getMessagesByReceiverId(receiverId);
    }

    // Gönderenin gönderdiği mesajları alma
    @GetMapping("/sender/{senderId}")
    public List<Message> getMessagesBySenderId(@PathVariable Long senderId) {
        return messageService.getMessagesBySenderId(senderId);
    }

    // Mesajı okundu olarak işaretleme
    @PutMapping("/markAsRead/{messageId}")
    public void markAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
    }

}
