package com.ubeli.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "kategori")
public class Kategori {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long kategoriId;

    private String namaKategori;

    // Satu Kategori punya banyak Produk
    @OneToMany(mappedBy = "kategori")
    private List<Produk> produks;
}