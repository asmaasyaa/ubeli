package com.ubeli.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import com.ubeli.enums.StatusPesanan; // Pastikan Enum sudah dibuat

@Entity
@Data
@Table(name = "pesanan")
public class Pesanan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pesananId;

    private BigDecimal totalHarga;
    private String buktiTransferUrl;

    // ENUM STATUS
    @Enumerated(EnumType.STRING)
    private StatusPesanan statusPesanan;

    // PEMBELI (Yang beli)
    @ManyToOne
    @JoinColumn(name = "pembeli_id")
    private Pembeli pembeli;

    // PENJUAL (Yang jual)
    @ManyToOne
    @JoinColumn(name = "penjual_id")
    private Penjual penjual;

    // ITEM BELANJAAN (Composition)
    @OneToMany(mappedBy = "pesanan", cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();
}