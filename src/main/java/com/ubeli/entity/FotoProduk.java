package com.ubeli.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "foto_produk")
public class FotoProduk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fotoId;

    private String urlFoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produk_id")
    private Produk produk;
}
