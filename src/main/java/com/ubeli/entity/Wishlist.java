package com.ubeli.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "wishlist")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishlistId;

    // MILIK SIAPA?
    @ManyToOne
    @JoinColumn(name = "pembeli_id")
    private Pembeli pembeli;

    // BARANGNYA APA?
    @ManyToOne
    @JoinColumn(name = "produk_id")
    private Produk produk;
}