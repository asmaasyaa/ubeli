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

    // USE CASE: CHECKOUT / AJUKAN PEMBELIAN
    // Skenario: Pembeli klik "Beli Sekarang" pada produk tertentu
    @PostMapping("/transaksi/checkout")
    public String prosesCheckout(@RequestParam Long pembeliId, 
                                 @RequestParam Long produkId, 
                                 @RequestParam int jumlah) {

        System.out.println("Checkout Berhasil! Pesanan dibuat untuk produk ID: " + produkId);

        return "redirect:/riwayat-pesanan"; // Arahkan ke halaman riwayat
    }

    // USE CASE: KONFIRMASI TERIMA BARANG
    // Skenario: Barang sampai, Pembeli klik "Pesanan Diterima"
    @PostMapping("/transaksi/konfirmasi-terima")
    public String konfirmasiTerima(@RequestParam Long pesananId) {
        
        System.out.println("Pesanan " + pesananId + " Selesai. Dana siap cair ke Penjual.");

        return "redirect:/riwayat-pesanan";
    }
}