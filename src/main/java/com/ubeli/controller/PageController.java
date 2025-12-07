package com.ubeli.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ubeli.entity.Produk;
import com.ubeli.entity.Kategori;
import com.ubeli.entity.Penjual;
import com.ubeli.entity.Notifikasi;

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

    @GetMapping("/notifikasi/{filter}")
    public String notifPage(@PathVariable String filter, Model model) {

        model.addAttribute("filter", filter);

        List<Notifikasi> list = new ArrayList<>();

        switch (filter) {

            case "jual":
                list.add(new Notifikasi(
                    "Hassan tertarik dengan produk Anda",
                    "iPhone 17 256GB Like New",
                    "29–10–2025 14:00",
                    "MENUNGGU"  // tombol terima & tolak muncul
                ));
                break;

            case "beli":
                list.add(new Notifikasi(
                    "Penjual menerima pengajuan Anda",   // ganti kata sesuai kamu: pengajuan
                    "iPhone 17 256GB Like New",
                    "29–10–2025 14:00",
                    "DITERIMA"   // tombol lanjutkan transaksi & batal muncul
                ));

                list.add(new Notifikasi(
                    "Penjual menolak pengajuan Anda",
                    "iPhone 17 256GB Like New",
                    "29–10–2025 14:00",
                    "DITOLAK"    // hanya status, no buttons
                ));
                break;

            case "pembayaran":
                list.add(new Notifikasi(
                    "Pembayaran berhasil, menunggu verifikasi admin",
                    "iPhone 17 256GB Like New",
                    "29–10–2025 14:00",
                    "MENUNGGU_VERIFIKASI"
                ));
                break;
        }

        model.addAttribute("listNotif", list);
        return "general/notifikasi";  // atau pembeli/notifikasi
    }
}
