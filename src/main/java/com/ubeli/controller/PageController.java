package com.ubeli.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ubeli.entity.Produk;
import com.ubeli.entity.Kategori;
import com.ubeli.entity.Penjual;

@Controller
public class PageController {

    // ROUTE UNTUK TEST HALAMAN CHECKOUT TANPA LOGIC BACKEND
    @GetMapping("/checkout")
    public String checkoutPage(Model model) {

        // --- PRODUK DUMMY UNTUK CEGAH ERROR THYMELEAF ---
        Produk dummy = new Produk();
        dummy.setNamaProduk("iPhone 17 256GB Like New");
        dummy.setHarga(new BigDecimal("20000000"));

        // dummy kategori
        Kategori kategori = new Kategori();
        kategori.setNamaKategori("Elektronik");
        dummy.setKategori(kategori);

        // dummy penjual
        Penjual penjual = new Penjual();
        penjual.setNamaLengkap("Fajar Pratama");
        dummy.setPenjual(penjual);

        // masukkan dummy ke model
        model.addAttribute("produk", dummy);

        // arahkan ke HTML
        return "pembeli/checkout";
    }
}
