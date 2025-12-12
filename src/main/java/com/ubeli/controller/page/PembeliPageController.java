package com.ubeli.controller.page;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ubeli.entity.Kategori;
import com.ubeli.entity.Penjual;
import com.ubeli.entity.Produk;

@Controller
public class PembeliPageController {
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
