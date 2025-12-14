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

    // ✅ BEST PRACTICE: Gunakan 'final' agar @RequiredArgsConstructor berfungsi
    // Tidak perlu @Autowired lagi di sini
    private final PesananRepository pesananRepo; 

    // ========================================================
    // 1. USE CASE: TAMPILKAN HALAMAN RIWAYAT
    // URL: /riwayat-pesanan
    // ========================================================
    @GetMapping("/riwayat-pesanan")
    public String halamanRiwayat(Model model, HttpSession session) {
        
        // 1. Cek Login (Support key "user" atau "pembeli" biar aman)
        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            userObj = session.getAttribute("pembeli");
        }

        // 2. Jika tetap null, tendang ke login
        if (userObj == null) {
            return "redirect:/login";
        }

        // 3. Pastikan yang login adalah Pembeli (Safety check)
        if (!(userObj instanceof Pembeli)) {
            // Jika admin/penjual nyasar ke sini, balikin ke home
            return "redirect:/home"; 
        }

        Pembeli pembeli = (Pembeli) userObj;

        // 4. Ambil Data Pesanan (Pakai Logic Kode Bawah yang BENAR)
        // Mengambil semua pesanan berdasarkan ID pembeli
        List<Pesanan> listPesanan = pesananRepo.findByPembeli_PembeliIdOrderByPesananIdDesc(pembeli.getPembeliId());
        
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
        }

        return "redirect:/riwayat-pesanan";
    }
}