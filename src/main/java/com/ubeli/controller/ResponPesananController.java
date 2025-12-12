package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ubeli.service.PesananService;
import jakarta.servlet.http.HttpSession; // Tambahkan import ini
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ResponPesananController {

    private final PesananService pesananService;

    // ==========================================
    // 1. AJUKAN PEMBELIAN (Pembeli)
    // ==========================================
    @PostMapping("/ajukan/{produkId}")
    public String ajukanPembelian(@PathVariable Long produkId,
                                  @RequestParam Long pembeliId,
                                  HttpSession session) { // Tambah Session
        
        // Cek Login Pembeli
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        try {
            pesananService.ajukanPembelian(produkId, pembeliId);
            return "redirect:/produk/" + produkId + "?success=ajuan-dikirim";
        } catch (RuntimeException e) {
            return "redirect:/produk/" + produkId + "?error=" + e.getMessage();
        }
    }

    // ==========================================
    // 2. TERIMA PENGAJUAN (Penjual)
    // ==========================================
    @GetMapping("/terima/{pesananId}")
    public String terimaPengajuan(@PathVariable Long pesananId, HttpSession session) {

        // Cek Login Penjual
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        // Panggil Service (Logic berat ada di sana)
        pesananService.terimaPengajuan(pesananId);

        // HAPUS BARIS: Long produkId = ... (INI YANG BIKIN ERROR)
        // Kita tidak butuh ID produk untuk redirect ke notifikasi

        return "redirect:/notifikasi?success=diterima";
    }

    // ==========================================
    // 3. TOLAK PENGAJUAN (Penjual)
    // ==========================================
    @GetMapping("/tolak/{pesananId}")
    public String tolakPengajuan(@PathVariable Long pesananId, HttpSession session) {

        // Cek Login Penjual
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        pesananService.tolakPengajuan(pesananId);

        return "redirect:/notifikasi?success=ditolak";
    }
}