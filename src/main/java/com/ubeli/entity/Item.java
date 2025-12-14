package com.ubeli.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    // RELASI KE PESANAN (
    @ManyToOne
    @JoinColumn(name = "pesanan_id")
    private Pesanan pesanan;

    // RELASI KE PRODUK
    @ManyToOne
    @JoinColumn(name = "produk_id")
    private Produk produk;

    // DATA TRANSAKSI
    private int jumlah; 
    
    private BigDecimal hargaSatuanSaatIni; 
    
    private BigDecimal subtotal; 

    public Item() {} 

    public Item(Pesanan pesanan, Produk produk, int jumlah) {
        this.pesanan = pesanan;
        this.produk = produk;
        this.jumlah = jumlah;
        this.hargaSatuanSaatIni = produk.getHarga(); 
        this.subtotal = this.hargaSatuanSaatIni.multiply(new BigDecimal(jumlah));
    }
}