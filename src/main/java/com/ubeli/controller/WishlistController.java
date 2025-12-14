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
import com.ubeli.repository.PembeliRepository;
import com.ubeli.repository.ProdukRepository;
import com.ubeli.repository.WishlistRepository;

@Controller
public class WishlistController {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private PembeliRepository pembeliRepository;

    @Autowired
    private ProdukRepository produkRepository;

    // USE CASE: TAMBAH KE WISHLIST
    @PostMapping("/wishlist/tambah")
    public String tambahWishlist(@RequestParam Long pembeliId, @RequestParam Long produkId) {
        Pembeli pembeli = pembeliRepository.findById(pembeliId).orElse(null);
        Produk produk = produkRepository.findById(produkId).orElse(null);

        if (pembeli == null || produk == null) {
            return "redirect:/error";
        }
        
        Wishlist existing = wishlistRepository.findByPembeliAndProduk(pembeli, produk);
        if (existing != null) {
            return "redirect:/wishlist-saya";
        }

        Wishlist w = new Wishlist();
        w.setPembeli(pembeli);
        w.setProduk(produk);

        wishlistRepository.save(w);

        return "redirect:/wishlist-saya";
    }

    // USE CASE: HAPUS DARI WISHLIST
    @PostMapping("/wishlist/hapus")
    public String hapusWishlist(@RequestParam Long wishlistId) {
        
        System.out.println("Wishlist ID " + wishlistId + " dihapus.");

        return "redirect:/wishlist-saya";
    }

    // USE CASE: LIHAT WISHLIST
    @GetMapping("/wishlist-saya")
    public String lihatWishlist(Model model, HttpSession session) {

        Long pembeliId = (Long) session.getAttribute("pembeliId");

        if (pembeliId == null) {
            model.addAttribute("wishlistProducts", Collections.emptyList());
            return "pembeli/wishlist";   
        }

        List<Wishlist> wishlistItems =
                wishlistRepository.findByPembeli_PembeliId(pembeliId);

        List<Produk> produkWishlist = wishlistItems.stream()
                .map(Wishlist::getProduk)
                .toList();

        model.addAttribute("wishlistProducts", produkWishlist);

        return "pembeli/wishlist";
    }

    @PostMapping("/wishlist/toggle")
    @ResponseBody
    public Map<String, Object> toggleWishlist(
            @RequestParam Long produkId,
            HttpSession session) {

        Map<String, Object> res = new HashMap<>();

        Pembeli pembeli = (Pembeli) session.getAttribute("pembeli");
        if (pembeli == null) {
            res.put("success", false);
            res.put("message", "NOT_LOGIN");
            return res;
        }

        Produk produk = produkRepository.findById(produkId).orElse(null);
        if (produk == null) {
            res.put("success", false);
            return res;
        }

        Wishlist existing =
            wishlistRepository.findByPembeliAndProduk(pembeli, produk);

        if (existing != null) {
            wishlistRepository.delete(existing);
            res.put("wishlisted", false);
        }
        else {
            Wishlist w = new Wishlist();
            w.setPembeli(pembeli);
            w.setProduk(produk);
            wishlistRepository.save(w);
            res.put("wishlisted", true);
        }

        res.put("success", true);
        return res;
    }

}