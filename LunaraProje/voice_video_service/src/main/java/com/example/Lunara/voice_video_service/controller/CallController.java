package com.example.Lunara.voice_video_service.controller;

import com.example.Lunara.voice_video_service.DTO.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class CallController {

    private final SimpMessagingTemplate messagingTemplate;

    public CallController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/room/join")
    @SendTo("/topic/room")
    public RoomJoinResponse joinRoom(RoomJoinRequest request) {
        return new RoomJoinResponse(request.getEmail(), request.getRoom());
    }

    @MessageMapping("/user/call")
    public void userCall(CallRequest request) {
        messagingTemplate.convertAndSendToUser(request.getTo(), "/queue/call", request);
    }

    @MessageMapping("/call/accepted")
    public void callAccepted(CallAcceptedRequest request) {
        messagingTemplate.convertAndSendToUser(request.getTo(), "/queue/call-accepted", request);
    }

    @MessageMapping("/peer/nego/needed")
    public void peerNegoNeeded(PeerNegoRequest request) {
        messagingTemplate.convertAndSendToUser(request.getTo(), "/queue/peer-nego", request);
    }

    @MessageMapping("/peer/nego/done")
    public void peerNegoDone(PeerNegoRequest request) {
        messagingTemplate.convertAndSendToUser(request.getTo(), "/queue/peer-nego-final", request);
    }
}


