package com.ubeli.entity;

import jakarta.persistence.*; // Import wajib buat database
import lombok.Data;           // Import Lombok (Otomatis Getter/Setter)

@Entity       // Menandakan ini Tabel Database
@Data         // Menandakan ini punya Getter, Setter, Constructor otomatis
@Table(name = "admin") // Opsional: Nama tabel di SQL
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId; 

    private String username;
    private String passwordHash;
}