package com.example;

import lombok.Data;

@Data
public class MessageRequest {
    private String clientId;
    private String message;
}