package com.ubeli.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Data
@Table(name = "produk")
public class Produk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long produkId;

    private String namaProduk;
    private String deskripsi;
    private BigDecimal harga;
    private String status; // Available/Sold
    private boolean diiklankan;
    private LocalDate periodeIklan;
    private String merk;
    private String kondisi;


    // RELASI KE PENJUAL (Many Products -> One Seller)
    @ManyToOne
    @JoinColumn(name = "penjual_id")
    private Penjual penjual;

    // RELASI KE KATEGORI
    @ManyToOne
    @JoinColumn(name = "kategori_id")
    private Kategori kategori;

    // RELASI KE FOTO (One Product -> Many Photos)
    // Cascade ALL: Hapus produk = Hapus fotonya
    @OneToMany(mappedBy = "produk", cascade = CascadeType.ALL)
    private List<FotoProduk> listFoto = new ArrayList<>();

    public void tambahFoto(String url) {
        FotoProduk foto = new FotoProduk();
        foto.setUrlFoto(url);
        foto.setProduk(this); // Sambungkan foto ke produk ini
        this.listFoto.add(foto);
    }
}