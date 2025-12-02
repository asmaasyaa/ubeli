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

    // RELASI KE PESANAN (INDUK)
    // Banyak item ada dalam satu pesanan
    @ManyToOne
    @JoinColumn(name = "pesanan_id")
    private Pesanan pesanan;

    // RELASI KE PRODUK
    // Item ini merujuk ke produk apa?
    @ManyToOne
    @JoinColumn(name = "produk_id")
    private Produk produk;

    // DATA TRANSAKSI
    private int jumlah; // Qty (misal: 2 pcs)
    
    // PENTING: Simpan harga saat checkout di sini!
    // Jangan cuma ambil dari produk.getHarga(), karena harga produk bisa berubah nanti.
    private BigDecimal hargaSatuanSaatIni; 
    
    private BigDecimal subtotal; // (jumlah * hargaSatuanSaatIni)

    // CONSTRUCTOR KHUSUS (Biar gampang pas coding Controller)
    public Item() {} // Constructor kosong wajib buat JPA

    public Item(Pesanan pesanan, Produk produk, int jumlah) {
        this.pesanan = pesanan;
        this.produk = produk;
        this.jumlah = jumlah;
        this.hargaSatuanSaatIni = produk.getHarga(); // Kunci harga saat ini
        this.subtotal = this.hargaSatuanSaatIni.multiply(new BigDecimal(jumlah));
    }
}