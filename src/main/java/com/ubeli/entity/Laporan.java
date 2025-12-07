package com.ubeli.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "laporan")
public class Laporan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long laporanId;

    // ALASAN & BUKTI
    private String alasan;
    private String deskripsi;
    private String buktiFotoUrl;
    private String status; // Pending, Selesai, Ditolak
    private LocalDate tanggalKejadian;

    // RELASI 1: PELAPOR (Pembeli yang melapor)
    // Menggantikan 'private int pelaporId'
    @ManyToOne
    @JoinColumn(name = "pelapor_id", nullable = false)
    private Pembeli pelapor;

    // RELASI 2: TERLAPOR (Penjual yang dilaporkan)
    // Menggantikan 'private int terlaporId'
    @ManyToOne
    @JoinColumn(name = "terlapor_id")
    private Penjual terlapor;

    // RELASI 3: PRODUK (Barang yang bermasalah)
    // Sesuai permintaanmu agar ada produknya
    @ManyToOne
    @JoinColumn(name = "produk_id")
    private Produk produk;
}