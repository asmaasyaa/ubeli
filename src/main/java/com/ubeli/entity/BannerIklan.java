package com.ubeli.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import com.ubeli.enums.StatusIklan; // Import Enum Status
import com.ubeli.enums.JenisPaket; // Import Enum Paket

@Entity
@Data
@Table(name = "banner_iklan")
public class BannerIklan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iklanId; // Ubah int jadi Long biar standar database

    // RELASI KE PRODUK
    // Satu produk bisa diiklankan berkali-kali (History), jadi ManyToOne
    @ManyToOne
    @JoinColumn(name = "produk_id", nullable = false)
    private Produk produk;

    // ENUM STATUS (Ini yang kamu tanyakan)
    @Enumerated(EnumType.STRING)
    private StatusIklan status; 

    // ENUM PAKET (Biar harga & durasi otomatis)
    @Enumerated(EnumType.STRING)
    private JenisPaket jenisPaket;

    // TANGGAL
    private LocalDate tanggalMulai;   // Kapan di-ACC admin
    private LocalDate tanggalSelesai; // Kapan expired (otomatis dihitung)

    // JANGAN ADA CONTROLLER DI SINI
    // private ValidasiIklanController validasiiklancontroller; <--- HAPUS PERMANEN
}