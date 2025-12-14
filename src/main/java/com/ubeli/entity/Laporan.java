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

    private String alasan;
    private String deskripsi;
    private String buktiFotoUrl;
    private String status; 
    private LocalDate tanggalKejadian;

    // RELASI 1: PELAPOR 
    @ManyToOne
    @JoinColumn(name = "pelapor_id", nullable = false)
    private Pembeli pelapor;

    // RELASI 2: TERLAPOR 
    @ManyToOne
    @JoinColumn(name = "terlapor_id")
    private Penjual terlapor;

    // RELASI 3: PRODUK 
    @ManyToOne
    @JoinColumn(name = "produk_id")
    private Produk produk;
}