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
import java.util.ArrayList;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;

@Controller
public class KatalogController {

    @Autowired private ProdukRepository produkRepo;
    @Autowired private BannerIklanRepository bannerRepo;
    @Autowired private WishlistRepository wishlistRepository;
    @Autowired private PembeliRepository pembeliRepository;

    @Autowired
    private ProdukRepository produkRepository;


    // 1. HALAMAN HOME
    @GetMapping({"/", "/home"})
    public String home(Model model, HttpSession session) {

        // 1. LOGIC BANNER & REKOMENDASI (Iklan Aktif)
        List<BannerIklan> iklanAktif = bannerRepo.findActiveAds(StatusIklan.ACTIVE, LocalDate.now());
        
        List<Produk> listBoosted = new ArrayList<>();
        for (BannerIklan iklan : iklanAktif) {
            listBoosted.add(iklan.getProduk());
        }

        // 2. LOGIC KATALOG BIASA (Hanya status Available)
        List<Produk> listSemua = produkRepo.findByStatusIgnoreCase("Available");

        // 3. KIRIM KE HTML
        model.addAttribute("listBoosted", listBoosted);
        model.addAttribute("produkList", listSemua);
        model.addAttribute("wishlistProdukIds", getWishlistIds(session));

        return "general/home";
    }

    // 2. HALAMAN DETAIL PRODUK
    @GetMapping("/produk/{id}")
    public String detailProduk(@PathVariable Long id, Model model, HttpSession session) { // TAMBAH HttpSession
        
        Produk produk = produkRepo.findById(id).orElse(null);
        if (produk == null) return "redirect:/katalog"; // Handle jika produk tidak ada
        
        model.addAttribute("p", produk);
        model.addAttribute("pemilik", produk.getPenjual());

        // Ambil Data Session
        Object userObj = session.getAttribute("user"); // Cek user login umum
        Penjual penjual = (Penjual) session.getAttribute("penjual");
        Long pembeliId = (Long) session.getAttribute("pembeliId"); // Ambil ID Pembeli jika ada
        String role = (String) session.getAttribute("role");

        if (role==null) 
            return "redirect:/login";

        boolean isWishlisted = false;

        if ("PENJUAL".equals(role)) {
            if (penjual != null && 
                produk.getPenjual().getPenjualId().equals(penjual.getPenjualId())) {
                
                model.addAttribute("penjual", penjual);
                return "penjual/detail-produk-penjual";
            }
        }

        // LOGIC 2: Jika PEMBELI melihat produk (Cek Wishlist)
        if (pembeliId != null) { // PERBAIKAN: Cek variabel pembeliId, bukan Class Pembeli
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
        // Gunakan produkRepo (konsisten)
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

    // 6. UTIL: AMBIL WISHLIST USER
    private Set<Long> getWishlistIds(HttpSession session) {
        // Cek login via atribut "pembeli" atau "user"
        Object userObj = session.getAttribute("pembeli"); 
        if (userObj == null) userObj = session.getAttribute("user");

        if (userObj == null || !(userObj instanceof Pembeli)) {
            return new HashSet<>();
        }

        Pembeli pembeli = (Pembeli) userObj;

        return wishlistRepository.findByPembeli_PembeliId(pembeli.getPembeliId())
                .stream()
                .map(w -> w.getProduk().getProdukId())
                .collect(Collectors.toSet());
    }

    // 7. LAPORAN (TIDAK DIUBAH)
    @GetMapping("/laporan/buat/{produkId}")
    public String buatLaporan(@PathVariable Long produkId, Model model) {
        Produk produk = produkRepo.findById(produkId).orElse(null);
        model.addAttribute("produk", produk);
        return "laporan/buat-laporan";
    }
}