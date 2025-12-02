package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import com.ubeli.entity.*;
import com.ubeli.enums.StatusPesanan;
// import com.ubeli.repository.*; // Pastikan bikin repo-nya nanti

import java.math.BigDecimal;
import java.util.List;

@Controller
public class TransaksiController {

    // Inject Repository (Pastikan file Repository-nya sudah dibuat ya!)
    // @Autowired
    // private PesananRepository pesananRepository;
    // @Autowired
    // private ProdukRepository produkRepository;
    // @Autowired
    // private PembeliRepository pembeliRepository;

    // USE CASE: CHECKOUT / AJUKAN PEMBELIAN
    // Skenario: Pembeli klik "Beli Sekarang" pada produk tertentu
    @PostMapping("/transaksi/checkout")
    public String prosesCheckout(@RequestParam Long pembeliId, 
                                 @RequestParam Long produkId, 
                                 @RequestParam int jumlah) {
        
        // 1. Ambil Data dari Database
        // Pembeli pembeli = pembeliRepository.findById(pembeliId).orElse(null);
        // Produk produk = produkRepository.findById(produkId).orElse(null);

        // Validasi sederhana
        // if (pembeli == null || produk == null) return "redirect:/error";

        // 2. Buat Pesanan Baru
        // Penjual penjual = produk.getPenjual(); // Ambil penjual dari produk
        // Pesanan pesanan = new Pesanan();
        // pesanan.setPembeli(pembeli);
        // pesanan.setPenjual(penjual);
        // pesanan.setStatusPesanan(StatusPesanan.MENUNGGU_KONFIRMASI_PENJUAL);

        // 3. Masukkan Item ke dalam Pesanan (Logika Class Item)
        // Item itemBaru = new Item(pesanan, produk, jumlah);
        // pesanan.getItems().add(itemBaru);

        // 4. Hitung Total Harga (Harga x Jumlah)
        // BigDecimal total = produk.getHarga().multiply(new BigDecimal(jumlah));
        // pesanan.setTotalHarga(total);

        // 5. Simpan ke Database
        // pesananRepository.save(pesanan);

        System.out.println("Checkout Berhasil! Pesanan dibuat untuk produk ID: " + produkId);

        return "redirect:/riwayat-pesanan"; // Arahkan ke halaman riwayat
    }

    // USE CASE: KONFIRMASI TERIMA BARANG
    // Skenario: Barang sampai, Pembeli klik "Pesanan Diterima"
    @PostMapping("/transaksi/konfirmasi-terima")
    public String konfirmasiTerima(@RequestParam Long pesananId) {
        
        // 1. Ambil Pesanan
        // Pesanan pesanan = pesananRepository.findById(pesananId).orElse(null);

        // 2. Ubah Status jadi SELESAI
        // if (pesanan != null) {
        //     pesanan.setStatusPesanan(StatusPesanan.SELESAI);
        //     pesananRepository.save(pesanan);
             System.out.println("Pesanan " + pesananId + " Selesai. Dana siap cair ke Penjual.");
        // }

        return "redirect:/riwayat-pesanan";
    }
}