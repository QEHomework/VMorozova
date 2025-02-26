package com.example;

import lombok.Data;

@Data
public class EnrichedMessage {
    private String clientId;
    private String message;
    private ClientData clientData;

    public EnrichedMessage(String clientId, String message, ClientData clientData) {
        this.clientId = clientId;
        this.message = message;
        this.clientData = clientData;
    }
}