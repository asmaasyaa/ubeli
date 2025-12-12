package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;

import com.ubeli.entity.*;
import com.ubeli.repository.*;
import com.ubeli.enums.*;

@Controller
@RequestMapping("/penjual/iklan") // Path Utama
public class IklanController {

    @Autowired private ProdukRepository produkRepo;
    @Autowired private BannerIklanRepository bannerRepo;

    // 1. TAMPILKAN HALAMAN (GET)
    @GetMapping("/ajukan")
    public String formAjukanIklan(@RequestParam Long produkId, Model model) {
        Produk produk = produkRepo.findById(produkId).orElse(null);
        
        if (produk != null) {
            model.addAttribute("p", produk);
            model.addAttribute("listPaket", JenisPaket.values());
            
            // PERBAIKAN DI SINI: Sesuaikan dengan nama file kamu
            return "penjual/pengajuan-iklan"; 
        }
        return "redirect:/penjual/profil";
    }

    // 2. PROSES FORM (POST)
    // Path ini (/penjual/iklan/proses) yang akan dipanggil di HTML
    @PostMapping("/proses")
    public String prosesAjukanIklan(@RequestParam Long produkId, 
                                    @RequestParam JenisPaket paketDipilih) {
        
        Produk produk = produkRepo.findById(produkId).orElse(null);
        if (produk != null) {
            BannerIklan iklan = new BannerIklan();
            iklan.setProduk(produk);
            iklan.setJenisPaket(paketDipilih);
            iklan.setStatus(StatusIklan.PENDING);
            bannerRepo.save(iklan);
        }
        return "redirect:/penjual/profil";
    }
}