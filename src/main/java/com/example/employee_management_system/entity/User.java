package com.example.employee_management_system.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String phone;
    private String role;


    public User() {}

    public User(String email, String password, String phone, String role) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
    }
}