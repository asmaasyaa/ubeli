package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;

import com.ubeli.entity.Produk;
import com.ubeli.repository.ProdukRepository;

import java.util.List;

@Controller
public class KatalogController {

    @Autowired
    private ProdukRepository produkRepository;

    // 1. HALAMAN UTAMA (HOME)
    // Menampilkan semua produk yang ada di database
    // @GetMapping("/")
    // public String home(Model model) {
    //     // Ambil semua data produk dari SQL Server
    //     List<Produk> listProduk = produkRepository.findAll();
        
    //     // Kirim data ke HTML dengan nama variabel 'produkList'
    //     model.addAttribute("produkList", listProduk);
        
    //     // Buka file: src/main/resources/templates/general/home.html
    //     return "general/home";
    // }
    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<Produk> listProduk = produkRepository.findAll();
        model.addAttribute("produkList", listProduk);
        return "general/home";
    }


    // 2. HALAMAN DETAIL PRODUK
    // Menampilkan info lengkap saat produk diklik
    @GetMapping("/produk/{id}")
    public String detailProduk(@PathVariable Long id, Model model) {
        // Cari produk berdasarkan ID, kalau gak ada return null
        Produk produk = produkRepository.findById(id).orElse(null);
        
        // Kirim data ke HTML
        model.addAttribute("p", produk);
        
        // Buka file: src/main/resources/templates/general/detail_produk.html
        return "general/detail_produk";
    }
}