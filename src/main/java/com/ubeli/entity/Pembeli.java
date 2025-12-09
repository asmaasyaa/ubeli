package com.ubeli.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

@Entity
@Data
@Table(name = "pembeli")
public class Pembeli {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pembeliId; // Ubah int jadi Long

    private String namaLengkap;
    private String email;
    private String passwordHash;
    private String noHp;
    private String status; // Active, Banned (Buat fitur blokir)

    // RELASI 1: WISHLIST (Satu pembeli punya banyak wishlist)
    @OneToMany(mappedBy = "pembeli", cascade = CascadeType.ALL)
    private List<Wishlist> wishlists = new ArrayList<>();

    // RELASI 2: PESANAN (Satu pembeli punya banyak riwayat pesanan)
    @OneToMany(mappedBy = "pembeli")
    private List<Pesanan> pesanans = new ArrayList<>();

    // RELASI 3: LAPORAN (Satu pembeli bisa melapor berkali-kali)
    @OneToMany(mappedBy = "pelapor")
    private List<Laporan> laporans = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDate createdAt = LocalDate.now();

}