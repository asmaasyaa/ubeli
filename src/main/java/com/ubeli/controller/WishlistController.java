package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ubeli.entity.*;
// import com.ubeli.repository.*; 

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
}