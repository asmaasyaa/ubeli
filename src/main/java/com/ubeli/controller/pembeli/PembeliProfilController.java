package com.ubeli.controller.pembeli;

import com.ubeli.entity.Pembeli;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PembeliProfilController {

    @GetMapping("/pembeli/profil")
    public String profilPembeli(HttpSession session, Model model) {

        if (!"PEMBELI".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        Pembeli pembeli = (Pembeli) session.getAttribute("pembeli");
        model.addAttribute("pembeli", pembeli);

        return "pembeli/profil2";
    }
}
