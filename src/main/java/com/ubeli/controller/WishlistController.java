package com.ubeli.controller;

import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

import com.ubeli.entity.*;
// import com.ubeli.repository.*; 
import com.ubeli.repository.PembeliRepository;
import com.ubeli.repository.WishlistRepository;

@Controller
public class WishlistController {

    // USE CASE: TAMBAH KE WISHLIST
    @PostMapping("/wishlist/tambah")
    public String tambahWishlist(@RequestParam Long pembeliId, @RequestParam Long produkId) {
        
        System.out.println("Produk " + produkId + " masuk ke wishlist pembeli " + pembeliId);

        return "redirect:/katalog"; 
    }

    // USE CASE: HAPUS DARI WISHLIST
    @PostMapping("/wishlist/hapus")
    public String hapusWishlist(@RequestParam Long wishlistId) {
        
        // wishlistRepository.deleteById(wishlistId);
        System.out.println("Wishlist ID " + wishlistId + " dihapus.");

        return "redirect:/wishlist-saya";
    }

    // USE CASE: LIHAT WISHLIST
    @Autowired
    private WishlistRepository wishlistRepository;

    @GetMapping("/wishlist-saya")
    public String lihatWishlist(Model model, HttpSession session) {

        // Ambil ID pembeli dari session
        Long pembeliId = (Long) session.getAttribute("pembeliId");

        // Kalau belum login → redirect ke login
        if (pembeliId == null) {
            model.addAttribute("wishlistProducts", Collections.emptyList());
            return "pembeli/wishlist";   // atau "redirect:/login";
        }

        // Ambil semua wishlist berdasarkan id pembeli
        List<Wishlist> wishlistItems =
                wishlistRepository.findByPembeli_PembeliId(pembeliId);

        // Extract Produk dari wishlist
        List<Produk> produkWishlist = wishlistItems.stream()
                .map(Wishlist::getProduk)
                .toList();

        model.addAttribute("wishlistProducts", produkWishlist);

        return "pembeli/wishlist";
    }
}