package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

import com.ubeli.entity.Produk;
import com.ubeli.entity.Wishlist;
import com.ubeli.entity.BannerIklan;
import com.ubeli.entity.Pembeli;
import com.ubeli.entity.Penjual;
import com.ubeli.repository.ProdukRepository;
import com.ubeli.repository.WishlistRepository;
import com.ubeli.repository.BannerIklanRepository;
import com.ubeli.repository.PembeliRepository;
import com.ubeli.enums.StatusIklan;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;

@Controller
public class KatalogController {

    @Autowired private ProdukRepository produkRepo;
    @Autowired private BannerIklanRepository bannerRepo;
    @Autowired private WishlistRepository wishlistRepository;
    @Autowired private PembeliRepository pembeliRepository;

    // 1. HALAMAN HOME
    @GetMapping({"/", "/home"})
    public String home(Model model, HttpSession session) {

        // --- OPTIMASI LOGIC BANNER ---
        List<BannerIklan> iklanAktif = bannerRepo.findActiveAds(StatusIklan.ACTIVE, LocalDate.now());
        
        // Pakai Stream biar lebih cepat & efisien
        List<Produk> listBoosted = iklanAktif.stream()
            .map(BannerIklan::getProduk) // Ambil produknya langsung
            .collect(Collectors.toList());

        // --- LOGIC KATALOG BIASA ---
        List<Produk> listSemua = produkRepo.findByStatusIgnoreCase("Available");

        // --- KIRIM KE HTML ---
        model.addAttribute("listBoosted", listBoosted);
        model.addAttribute("produkList", listSemua);
        model.addAttribute("wishlistProdukIds", getWishlistIds(session));

        return "general/home";
    }

    // 2. HALAMAN DETAIL PRODUK
    @GetMapping("/produk/{id}")
    public String detailProduk(@PathVariable Long id, Model model, HttpSession session) {
        
        Produk produk = produkRepo.findById(id).orElse(null);
        if (produk == null) return "redirect:/katalog"; 
        
        model.addAttribute("p", produk);
        model.addAttribute("pemilik", produk.getPenjual());

        // Ambil Data Session
        Penjual penjual = (Penjual) session.getAttribute("penjual");
        Long pembeliId = (Long) session.getAttribute("pembeliId"); 
        String role = (String) session.getAttribute("role");

        // Jika user belum login, tetap boleh lihat detail (Guest Mode)
        // Kalau mau wajib login, uncomment baris di bawah:
        // if (role == null) return "redirect:/login";

        boolean isWishlisted = false;

        // Jika Penjual melihat produk sendiri
        if ("PENJUAL".equals(role) && penjual != null) {
            if (produk.getPenjual().getPenjualId().equals(penjual.getPenjualId())) {
                model.addAttribute("penjual", penjual);
                return "penjual/detail-produk-penjual";
            }
        }

        // Jika Pembeli melihat produk (Cek Wishlist)
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

    // 3. HALAMAN KATALOG
    @GetMapping("/katalog")
    public String katalog(Model model, HttpSession session) {
        List<Produk> produkList = produkRepo.findByStatusIgnoreCase("Available");

        model.addAttribute("judulHalaman", "Semua Produk");
        model.addAttribute("produkList", produkList);
        model.addAttribute("wishlistProdukIds", getWishlistIds(session));

        return "general/katalog";
    }

    // 4. SEARCH PRODUK
    @GetMapping("/katalog/search")
    public String searchProduk(@RequestParam("q") String keyword, Model model, HttpSession session) {
        
        List<Produk> hasil = produkRepo.findByNamaProdukContainingAndStatusIgnoreCase(keyword, "Available");

        model.addAttribute("judulHalaman", "Hasil Pencarian: " + keyword);
        model.addAttribute("produkList", hasil);
        model.addAttribute("wishlistProdukIds", getWishlistIds(session));

        return "general/katalog";
    }

    // 5. FILTER KATEGORI
    @GetMapping("/katalog/kategori/{id}")
    public String filterKategori(@PathVariable Long id, Model model, HttpSession session) {
        
        List<Produk> produkKategori = produkRepo.findByKategori_KategoriIdAndStatusIgnoreCase(id, "Available");

        model.addAttribute("judulHalaman", "Kategori Produk");
        model.addAttribute("produkList", produkKategori);
        model.addAttribute("wishlistProdukIds", getWishlistIds(session));

        return "general/katalog";
    }

    // 6. UTIL: AMBIL WISHLIST USER (Safe Method)
    private Set<Long> getWishlistIds(HttpSession session) {
        Object userObj = session.getAttribute("pembeli"); 
        
        // Logic Fallback: Jika session pembeli null, coba cari session user biasa
        if (userObj == null) userObj = session.getAttribute("user");

        if (userObj == null || !(userObj instanceof Pembeli)) {
            return new HashSet<>(); // Return set kosong jika belum login/bukan pembeli
        }

        Pembeli pembeli = (Pembeli) userObj;

        // Ambil ID Produk yang ada di wishlist user ini
        return wishlistRepository.findByPembeli_PembeliId(pembeli.getPembeliId())
                .stream()
                .map(w -> w.getProduk().getProdukId())
                .collect(Collectors.toSet());
    }

    // 7. LAPORAN
    @GetMapping("/laporan/buat/{produkId}")
    public String buatLaporan(@PathVariable Long produkId, Model model) {
        Produk produk = produkRepo.findById(produkId).orElse(null);
        model.addAttribute("produk", produk);
        return "laporan/buat-laporan";
    }
}