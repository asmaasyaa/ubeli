package com.ubeli.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notifikasi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // PRIMARY KEY wajib

    private String judul;

    private String subJudul;

    private String waktu;

    private String status;

    public Notifikasi(String judul, String subJudul, String waktu, String status) {
    this.judul = judul;
    this.subJudul = subJudul;
    this.waktu = waktu;
    this.status = status;
}

}
