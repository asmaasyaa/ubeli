package com.ubeli.controller.pembeli;

import com.ubeli.entity.Pembeli;
import com.ubeli.repository.PembeliRepository;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class PembeliProfilController {

    @Autowired
    private PembeliRepository pembeliRepository;

    @GetMapping("/pembeli/profil")
    public String profilPembeli(HttpSession session, Model model) {

        if (!"PEMBELI".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        Pembeli pembeliSession = (Pembeli) session.getAttribute("pembeli");
        if (pembeliSession == null) {
            return "redirect:/login";
        }

        // 🚀 AMBIL ULANG DARI DATABASE BIAR RELASI WISHLIST KELOAD
        Pembeli pembeli = pembeliRepository.findById(pembeliSession.getPembeliId())
                .orElseThrow(() -> new RuntimeException("Pembeli tidak ditemukan"));

        model.addAttribute("pembeli", pembeli);

        return "pembeli/profil";
    }
}
