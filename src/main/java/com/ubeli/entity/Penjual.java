package com.ubeli.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

@Entity
@Data
@Table(name = "penjual")
public class Penjual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long penjualId;

    private String namaLengkap; // Bisa dipakai sebagai nama toko
    private String email;
    private String passwordHash;
    private String noHp;

    private String lokasiToko;       // contoh: Lowokwaru, Malang
    private String deskripsiToko;    // bio/deskripsi toko

    private String bank;
    private String noRekening;
    private String status; // Active, Banned
    
    @Column(nullable = false, updatable = false)
    private LocalDate createdAt = LocalDate.now();


    @OneToMany(mappedBy = "penjual", cascade = CascadeType.ALL)
    private List<Produk> produks = new ArrayList<>();

    @OneToMany(mappedBy = "penjual")
    private List<Pesanan> pesananMasuk = new ArrayList<>();

    @OneToMany(mappedBy = "terlapor")
    private List<Laporan> laporans = new ArrayList<>();
}
