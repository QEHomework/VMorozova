package com.example;

import lombok.Data;

@Data
public class MessageResponse {
    private String status;
    private String enrichedMessage;

    public MessageResponse(String status, String enrichedMessage) {
        this.status = status;
        this.enrichedMessage = enrichedMessage;
    }
}