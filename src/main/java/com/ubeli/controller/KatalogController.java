package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;

import com.ubeli.entity.Produk;
import com.ubeli.entity.BannerIklan;
import com.ubeli.repository.ProdukRepository;
import com.ubeli.repository.BannerIklanRepository;
import com.ubeli.enums.StatusIklan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class KatalogController {

    @Autowired private ProdukRepository produkRepo;
    @Autowired private BannerIklanRepository bannerRepo; // <-- Tambah ini

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        
        // 1. LOGIC BANNER & REKOMENDASI
        // Ambil data iklan yang valid hari ini
        List<BannerIklan> iklanAktif = bannerRepo.findActiveAds(StatusIklan.ACTIVE, LocalDate.now());
        
        // Ekstrak Produk-nya saja (Biar gampang dipake di HTML)
        List<Produk> listBoosted = new ArrayList<>();
        for (BannerIklan iklan : iklanAktif) {
            listBoosted.add(iklan.getProduk());
        }

        // 2. LOGIC KATALOG BIASA
        List<Produk> listSemua = produkRepo.findAll();

        // 3. KIRIM KE HTML
        model.addAttribute("listBoosted", listBoosted); // Ini buat Section Atas (Banner & Rekomendasi)
        model.addAttribute("produkList", listSemua);    // Ini buat Section Bawah (Semua)
        
        return "general/home";
    }    

    // 2. HALAMAN DETAIL PRODUK
    // Menampilkan info lengkap saat produk diklik
    @GetMapping("/produk/{id}")
    public String detailProduk(@PathVariable Long id, Model model) {
        // Cari produk berdasarkan ID, kalau gak ada return null
        Produk produk = produkRepo.findById(id).orElse(null);
        
        // Kirim data ke HTML
        model.addAttribute("p", produk);
        
        // Buka file: src/main/resources/templates/general/detail_produk.html
        return "general/detail-produk";
    }
}