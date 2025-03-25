package com.example.Lunara.voice_video_service.DTO;

public class RoomJoinResponse {
    private String email;
    private String room;

    public RoomJoinResponse(String email, String room) {
        this.email = email;
        this.room = room;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
