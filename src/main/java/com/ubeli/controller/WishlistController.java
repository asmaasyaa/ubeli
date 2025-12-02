package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ubeli.entity.*;
// import com.ubeli.repository.*; 

@Controller
public class WishlistController {

    // @Autowired
    // private WishlistRepository wishlistRepository;
    // @Autowired
    // private ProdukRepository produkRepository;
    // @Autowired
    // private PembeliRepository pembeliRepository;

    // USE CASE: TAMBAH KE WISHLIST
    @PostMapping("/wishlist/tambah")
    public String tambahWishlist(@RequestParam Long pembeliId, @RequestParam Long produkId) {
        
        // 1. Ambil Data
        // Pembeli pembeli = pembeliRepository.findById(pembeliId).orElse(null);
        // Produk produk = produkRepository.findById(produkId).orElse(null);

        // 2. Simpan ke Database Wishlist
        // if (pembeli != null && produk != null) {
        //     Wishlist wishlistBaru = new Wishlist();
        //     wishlistBaru.setPembeli(pembeli);
        //     wishlistBaru.setProduk(produk);
             
        //     wishlistRepository.save(wishlistBaru);
             System.out.println("Produk " + produkId + " masuk ke wishlist pembeli " + pembeliId);
        // }

        return "redirect:/katalog"; // Balik lagi belanja
    }

    // USE CASE: HAPUS DARI WISHLIST
    @PostMapping("/wishlist/hapus")
    public String hapusWishlist(@RequestParam Long wishlistId) {
        
        // wishlistRepository.deleteById(wishlistId);
        System.out.println("Wishlist ID " + wishlistId + " dihapus.");

        return "redirect:/wishlist-saya";
    }
}