package com.example.Lunara.voice_video_service.DTO;

public class CallRequest {
    private String to;
    private String offer;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }
}
