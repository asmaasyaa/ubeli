package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

import com.ubeli.entity.Pembeli;
import com.ubeli.entity.Penjual;
import com.ubeli.entity.Produk;
import com.ubeli.entity.Wishlist;
import com.ubeli.repository.PembeliRepository;
import com.ubeli.repository.ProdukRepository;
import com.ubeli.repository.WishlistRepository;

import jakarta.servlet.http.HttpSession;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class KatalogController {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private PembeliRepository pembeliRepository;

    @Autowired
    private ProdukRepository produkRepository;


    // 1. HALAMAN HOME
    @GetMapping({"/", "/home"})
    public String home(Model model, HttpSession session) {

        // Tampilkan hanya produk AVAILABLE
        List<Produk> listProduk = produkRepository.findByStatusIgnoreCase("Available");
        model.addAttribute("produkList", listProduk);

        // Wishlist user
        model.addAttribute("wishlistProdukIds", getWishlistIds(session));

        return "general/home";
    }

    // 2. HALAMAN DETAIL PRODUK
    @GetMapping("/produk/{id}")
    public String detailProduk(
            @PathVariable Long id,
            HttpSession session,
            Model model
    ) {
        Produk produk = produkRepository.findById(id).orElse(null);

        model.addAttribute("p", produk);
        model.addAttribute("pemilik", produk.getPenjual());

        Penjual penjual = (Penjual) session.getAttribute("penjual");
        String role = (String) session.getAttribute("role");
        Long pembeliId = (Long) session.getAttribute("pembeliId");

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

        if (pembeliId != null) {
            Pembeli pembeli = pembeliRepository.findById(pembeliId).orElse(null);
            Wishlist w = wishlistRepository.findByPembeliAndProduk(pembeli, produk);
            isWishlisted = (w != null);
        }

        model.addAttribute("isWishlisted", isWishlisted);

        return "general/detail-produk";
    }

    // 3. HALAMAN KATALOG
    @GetMapping("/katalog")
    public String katalog(Model model, HttpSession session) {

        List<Produk> produkList = produkRepository.findByStatusIgnoreCase("Available");

        model.addAttribute("judulHalaman", "Semua Produk");
        model.addAttribute("produkList", produkList);
        model.addAttribute("wishlistProdukIds", getWishlistIds(session));

        return "general/katalog";
    }

    // 4. SEARCH PRODUK
    @GetMapping("/katalog/search")
    public String searchProduk(
            @RequestParam("q") String keyword,
            Model model,
            HttpSession session
    ) {
        List<Produk> hasil = produkRepository
                .findByNamaProdukContainingAndStatusIgnoreCase(keyword, "Available");

        model.addAttribute("judulHalaman", "Hasil Pencarian: " + keyword);
        model.addAttribute("produkList", hasil);
        model.addAttribute("wishlistProdukIds", getWishlistIds(session));

        return "general/katalog";
    }

    // 5. FILTER KATEGORI
    @GetMapping("/katalog/kategori/{id}")
    public String filterKategori(
            @PathVariable Long id,
            Model model,
            HttpSession session
    ) {
        List<Produk> produkKategori =
                produkRepository.findByKategori_KategoriIdAndStatusIgnoreCase(id, "Available");

        model.addAttribute("judulHalaman", "Kategori Produk");
        model.addAttribute("produkList", produkKategori);
        model.addAttribute("wishlistProdukIds", getWishlistIds(session));

        return "general/katalog";
    }

    // 6. UTIL: AMBIL WISHLIST USER
    private Set<Long> getWishlistIds(HttpSession session) {

        Pembeli pembeli = (Pembeli) session.getAttribute("pembeli");

        if (pembeli == null) return new HashSet<>();

        return wishlistRepository.findByPembeli_PembeliId(pembeli.getPembeliId())
                .stream()
                .map(w -> w.getProduk().getProdukId())
                .collect(Collectors.toSet());
    }

    // 7. LAPORAN (TIDAK DIUBAH)
    @GetMapping("/laporan/buat/{produkId}")
    public String buatLaporan(@PathVariable Long produkId, Model model) {
        Produk produk = produkRepository.findById(produkId).orElse(null);
        model.addAttribute("produk", produk);
        return "laporan/buat-laporan";
    }

}
