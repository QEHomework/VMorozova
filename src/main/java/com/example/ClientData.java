package com.example;

import lombok.Data;

@Data
public class ClientData {
    private String name;
    private String email;
    private String phone;

    public ClientData(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}