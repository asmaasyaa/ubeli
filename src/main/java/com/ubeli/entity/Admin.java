package com.ubeli.entity;

import jakarta.persistence.*; 
import lombok.Data;           

@Entity       
@Data         
@Table(name = "admin") 
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId; 

    private String username;
    private String passwordHash;
}