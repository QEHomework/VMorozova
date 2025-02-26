package com.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "clients")
public class Client {
    @Id
    private String clientId;
    private String name;
    private String email;
    private String phone;
}