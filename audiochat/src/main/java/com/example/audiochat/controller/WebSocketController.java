package com.example.audiochat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping("/audio")
    @SendTo("/topic/audio")
    public String sendAudio(String message) {
        return message;
    }
}
