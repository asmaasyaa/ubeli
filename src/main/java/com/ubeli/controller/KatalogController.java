package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;

import com.ubeli.entity.Produk;
import com.ubeli.entity.Pembeli;
import com.ubeli.entity.Penjual;
import com.ubeli.entity.Wishlist;

import com.ubeli.repository.ProdukRepository;
import com.ubeli.repository.WishlistRepository;
import com.ubeli.repository.PembeliRepository;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class KatalogController {

    @Autowired
    private ProdukRepository produkRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private PembeliRepository pembeliRepository;

    // -----------------------
    // 1) HOME ("/" atau "/home")
    // - menampilkan produk yang status = "Available"
    // - section "produk terpopuler / boosted" memakai produk diiklankan (diiklankan == true)
    // -----------------------
    @GetMapping({"/", "/home"})
    public String home(Model model, HttpSession session) {

        // semua produk (kita filter Available di Java supaya tidak tergantung method repo tambahan)
        List<Produk> semua = produkRepository.findAll();
        List<Produk> produkAvailable = semua.stream()
                .filter(p -> p != null && p.getStatus() != null && p.getStatus().equalsIgnoreCase("Available"))
                .collect(Collectors.toList());

        // boosted (diiklankan) dari repo (method findByDiiklankanTrue() ada di repo kamu)
        List<Produk> boosted = produkRepository.findByDiiklankanTrue();
        List<Produk> boostedAvailable = boosted.stream()
                .filter(p -> p != null && p.getStatus() != null && p.getStatus().equalsIgnoreCase("Available"))
                .collect(Collectors.toList());

        model.addAttribute("produkList", produkAvailable);
        model.addAttribute("listBoosted", boostedAvailable);
        model.addAttribute("wishlistProdukIds", getWishlistIds(session));

        return "general/home";
    }

    // -----------------------
    // 2) DETAIL PRODUK
    // - butuh session (untuk cek pembeli/penjual dan wishlist)
    // -----------------------
    @GetMapping("/produk/{id}")
    public String detailProduk(@PathVariable Long id, Model model, HttpSession session) {
        Produk produk = produkRepository.findById(id).orElse(null);

        if (produk == null) {
            // kalau ingin, kamu bisa redirect ke halaman 404/custom. Untuk sekarang kembalikan katalog.
            return "redirect:/katalog";
        }

        model.addAttribute("p", produk);
        model.addAttribute("pemilik", produk.getPenjual());

        // ambil info role & user dari session (sesuaikan nama attribute session jika beda)
        Penjual penjual = (Penjual) session.getAttribute("penjual");
        String role = (String) session.getAttribute("role");
        Long pembeliId = (Long) session.getAttribute("pembeliId");

        boolean isWishlisted = false;

        // jika yg buka adalah penjual pemilik produk -> tampilan penjual
        if ("PENJUAL".equals(role) && penjual != null && produk.getPenjual() != null
                && produk.getPenjual().getPenjualId().equals(penjual.getPenjualId())) {

            model.addAttribute("penjual", penjual);
            return "penjual/detail-produk-penjual";
        }

        // jika pembeli login -> cek wishlist
        if (pembeliId != null) {
            Pembeli pembeli = pembeliRepository.findById(pembeliId).orElse(null);
            if (pembeli != null) {
                Wishlist w = wishlistRepository.findByPembeliAndProduk(pembeli, produk);
                isWishlisted = (w != null);
            }
        }

        model.addAttribute("isWishlisted", isWishlisted);
        return "general/detail-produk";
    }

    // -----------------------
    // 3) KATALOG (lihat semua produk)
    // - hanya tampilkan produk Available
    // -----------------------
    @GetMapping("/katalog")
    public String katalog(Model model, HttpSession session) {

        List<Produk> semua = produkRepository.findAll();
        List<Produk> produkAvailable = semua.stream()
                .filter(p -> p != null && p.getStatus() != null && p.getStatus().equalsIgnoreCase("Available"))
                .collect(Collectors.toList());

        model.addAttribute("judulHalaman", "Semua Produk");
        model.addAttribute("produkList", produkAvailable);
        model.addAttribute("wishlistProdukIds", getWishlistIds(session));

        return "general/katalog";
    }

    // -----------------------
    // 4) SEARCH
    // - pakai repo.findByNamaProdukContaining(...) (ada di repo kamu)
    // - lalu filter status Available di Java
    // -----------------------
    @GetMapping("/katalog/search")
    public String searchProduk(@RequestParam("q") String keyword, Model model, HttpSession session) {

        List<Produk> hasil = produkRepository.findByNamaProdukContaining(keyword == null ? "" : keyword);
        List<Produk> hasilAvailable = hasil.stream()
                .filter(p -> p != null && p.getStatus() != null && p.getStatus().equalsIgnoreCase("Available"))
                .collect(Collectors.toList());

        model.addAttribute("judulHalaman", "Hasil Pencarian: " + keyword);
        model.addAttribute("produkList", hasilAvailable);
        model.addAttribute("wishlistProdukIds", getWishlistIds(session));

        return "general/katalog";
    }

    // -----------------------
    // 5) FILTER BY KATEGORI
    // - pakai repo.findByKategori_KategoriId(...) (ada di repo kamu)
    // - lalu filter status Available
    // -----------------------
    @GetMapping("/katalog/kategori/{id}")
    public String filterKategori(@PathVariable Long id, Model model, HttpSession session) {

        List<Produk> byKat = produkRepository.findByKategori_KategoriId(id);
        List<Produk> byKatAvailable = byKat.stream()
                .filter(p -> p != null && p.getStatus() != null && p.getStatus().equalsIgnoreCase("Available"))
                .collect(Collectors.toList());

        model.addAttribute("judulHalaman", "Kategori Produk");
        model.addAttribute("produkList", byKatAvailable);
        model.addAttribute("wishlistProdukIds", getWishlistIds(session));

        return "general/katalog";
    }

    // -----------------------
    // Util: ambil wishlist id set user (supaya view tahu mana yg sudah di-wishlist)
    // -----------------------
    private Set<Long> getWishlistIds(HttpSession session) {
        Pembeli pembeli = (Pembeli) session.getAttribute("pembeli");
        if (pembeli == null) return new HashSet<>();

        List<Wishlist> wishlist = wishlistRepository.findByPembeli_PembeliId(pembeli.getPembeliId());
        return wishlist.stream()
                .filter(w -> w != null && w.getProduk() != null && w.getProduk().getProdukId() != null)
                .map(w -> w.getProduk().getProdukId())
                .collect(Collectors.toSet());
    }

    // -----------------------
    // Laporan buat produk (tidak berubah)
    // -----------------------
    @GetMapping("/laporan/buat/{produkId}")
    public String buatLaporan(@PathVariable Long produkId, Model model) {
        Produk produk = produkRepository.findById(produkId).orElse(null);
        model.addAttribute("produk", produk);
        return "laporan/buat-laporan";
    }

}
