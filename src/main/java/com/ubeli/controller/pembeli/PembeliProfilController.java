package com.ubeli.controller.pembeli;

import com.ubeli.entity.Pembeli;
import com.ubeli.repository.PembeliRepository;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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

        Pembeli pembeli = pembeliRepository.findById(pembeliSession.getPembeliId())
                .orElseThrow(() -> new RuntimeException("Pembeli tidak ditemukan"));

        model.addAttribute("pembeli", pembeli);

        return "pembeli/profil";
    }

    @GetMapping("/pembeli/edit-profil")
    public String editProfil(HttpSession session, Model model) {

        // CEK ROLE
        if (!"PEMBELI".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        Pembeli pembeli = (Pembeli) session.getAttribute("pembeli");

        if (pembeli == null) {
            session.invalidate();
            return "redirect:/login";
        }

        model.addAttribute("pembeli", pembeli);

        return "pembeli/edit-profil";
    }

    @PostMapping("/pembeli/update-profil")
    public String updateProfil(
            @RequestParam String namaLengkap,
            @RequestParam String email,
            @RequestParam String noHp,
            HttpSession session
    ) {

        // AMBIL PEMBELI DARI SESSION
        Pembeli pembeli = (Pembeli) session.getAttribute("pembeli");

        if (pembeli == null) {
            session.invalidate();
            return "redirect:/login";
        }

        // UPDATE DATA
        pembeli.setNamaLengkap(namaLengkap);
        pembeli.setEmail(email);
        pembeli.setNoHp(noHp);
        
        // SIMPAN DATABASE
        pembeliRepository.save(pembeli);

        // UPDATE SESSION AGAR LANGSUNG TERLIHAT
        session.setAttribute("pembeli", pembeli);

        // REDIRECT DENGAN ALERT SUKSES
        return "redirect:/pembeli/profil?success";
    }
}
