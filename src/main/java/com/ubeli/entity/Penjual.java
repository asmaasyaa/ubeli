package com.ubeli.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Entity
@Data
@Table(name = "penjual")
public class Penjual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long penjualId;

    private String namaLengkap; // Nama Toko atau Nama Orang
    private String email;
    private String passwordHash;
    private String noHp;
    private String bank;
    private String noRekening; // Wajib buat penjual
    private String status; // Active, Banned

    // RELASI 1: PRODUK (Satu penjual punya banyak dagangan)
    @OneToMany(mappedBy = "penjual", cascade = CascadeType.ALL)
    private List<Produk> produks = new ArrayList<>();

    // RELASI 2: PESANAN MASUK (Satu penjual menerima banyak order)
    @OneToMany(mappedBy = "penjual")
    private List<Pesanan> pesananMasuk = new ArrayList<>();

    // RELASI 3: LAPORAN MASUK (Satu penjual bisa dilaporkan berkali-kali)
    @OneToMany(mappedBy = "terlapor")
    private List<Laporan> laporans = new ArrayList<>();
}