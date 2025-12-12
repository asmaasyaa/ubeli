package com.ubeli.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ubeli.enums.StatusPesanan;
import com.ubeli.enums.StatusPengajuan;

@Entity
@Data
@Table(name = "pesanan")
public class Pesanan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pesananId;

    private BigDecimal totalHarga;
    private String buktiTransferUrl;

    // STATUS PENGAJUAN (Baru Ditambah)
    @Enumerated(EnumType.STRING)
    private StatusPengajuan statusPengajuan;

    // STATUS PESANAN (Transaksi setelah diterima)
    @Enumerated(EnumType.STRING)
    private StatusPesanan statusPesanan; 

    // RELASI PRODUK
    @ManyToOne
    @JoinColumn(name = "produk_id")
    private Produk produk;

    // RELASI PEMBELI (pengaju)
    @ManyToOne
    @JoinColumn(name = "pembeli_id")
    private Pembeli pembeli;

    // RELASI PENJUAL
    @ManyToOne
    @JoinColumn(name = "penjual_id")
    private Penjual penjual;

    // ITEM (untuk transaksi multi produk)
    @OneToMany(mappedBy = "pesanan", cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();
}
