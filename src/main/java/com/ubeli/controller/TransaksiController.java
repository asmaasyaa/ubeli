package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.ubeli.entity.Pembeli;
import com.ubeli.entity.Pesanan;
import com.ubeli.enums.StatusPesanan;
import com.ubeli.repository.PesananRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TransaksiController {

    private final PesananRepository pesananRepo;

    // ========================================================
    // ⚠️ LOGIC "AJUKAN PEMBELIAN" SUDAH DIHAPUS DARI SINI
    // KARENA SUDAH DITANGANI OLEH 'AjukanPembelianController.java'
    // ========================================================


    // ========================================================
    // 1. USE CASE: TAMPILKAN HALAMAN RIWAYAT
    // URL: /riwayat-pesanan
    // ========================================================
    @GetMapping("/riwayat-pesanan")
    public String halamanRiwayat(Model model, HttpSession session) {
        
        // 1. Ambil User dari Session
        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            userObj = session.getAttribute("pembeli");
        }

        // 2. Cek Login
        if (userObj == null) {
            return "redirect:/login";
        }

        // 3. Casting ke Pembeli
        Pembeli pembeli = (Pembeli) userObj;

        // 4. Ambil Data Pesanan (PERBAIKAN DI SINI)
        // Kita ambil SEMUA pesanan milik pembeli ini, urut dari yang paling baru
        // Jangan filter pakai ID Produk (p), karena kita mau lihat semua sejarah belanja.
        
        List<Pesanan> listPesanan = pesananRepo.findByPembeli_PembeliIdAndProduk_ProdukIdAndStatusPengajuan(null, null, null);
        
        model.addAttribute("listPesanan", listPesanan);
        model.addAttribute("pembeli", pembeli); 

        return "pembeli/riwayat-pesanan"; 
    }

    // ========================================================
    // 2. USE CASE: KONFIRMASI TERIMA BARANG
    // URL: /transaksi/konfirmasi-terima
    // ========================================================
    @PostMapping("/transaksi/konfirmasi-terima")
    public String konfirmasiTerima(@RequestParam Long pesananId, HttpSession session) {
        
        // Cek Login sederhana
        if (session.getAttribute("user") == null && session.getAttribute("pembeli") == null) {
            return "redirect:/login";
        }

        Pesanan pesanan = pesananRepo.findById(pesananId).orElse(null);

        if (pesanan != null) {
            // Ubah Status jadi SELESAI
            pesanan.setStatusPesanan(StatusPesanan.SELESAI);
            pesananRepo.save(pesanan);

            // System.out.println("Pesanan Selesai. Dana diteruskan ke Penjual.");
        }

        return "redirect:/riwayat-pesanan";
    }
}