package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import com.ubeli.entity.*;
import com.ubeli.enums.StatusPesanan;
// import com.ubeli.repository.*; // Pastikan bikin repo-nya nanti
import com.ubeli.repository.PesananRepository;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class TransaksiController {

    @Autowired
    private PesananRepository pesananRepo;

    // USE CASE: KONFIRMASI TERIMA BARANG
    @PostMapping("/transaksi/konfirmasi-terima")
    public String konfirmasiTerima(@RequestParam Long pesananId) {
        
        System.out.println("Pesanan " + pesananId + " Selesai. Dana siap cair ke Penjual.");

        return "redirect:/riwayat-pesanan";
    }

    @GetMapping("/riwayat-pesanan")
    public String halamanRiwayat(Model model, HttpSession session) {

        String role = (String) session.getAttribute("role");

        if (role == null) {
            return "redirect:/login";
        }

        if (!role.equals("PEMBELI")) {
            return "redirect:/home";
        }

        Pembeli pembeli = (Pembeli) session.getAttribute("pembeli");

        if (pembeli == null) {
            return "redirect:/login";
        }

        List<Pesanan> listPesanan =
                pesananRepo.findByPembeli_PembeliId(
                        pembeli.getPembeliId()
                );

        model.addAttribute("listPesanan", listPesanan);
        model.addAttribute("pembeli", pembeli);

        return "pembeli/riwayat-pesanan";
    }   
}