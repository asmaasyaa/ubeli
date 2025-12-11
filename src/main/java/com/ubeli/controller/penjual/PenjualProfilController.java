package com.ubeli.controller.penjual;

import com.ubeli.entity.Penjual;
import com.ubeli.entity.Produk;
import com.ubeli.repository.ProdukRepository;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PenjualProfilController {

    @Autowired
    private ProdukRepository produkRepo;

    @GetMapping("/penjual/profil")
    public String profilPenjual(HttpSession session, Model model) {

        // CEK ROLE
        if (!"PENJUAL".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        // CEK OBJEK PENJUAL DI SESSION
        Penjual penjual = (Penjual) session.getAttribute("penjual");

        if (penjual == null) {  
            // kalau null, langsung logout untuk bersihkan session rusak
            session.invalidate();
            return "redirect:/login";
        }

        // AMBIL PRODUK
        List<Produk> produkList = produkRepo.findByPenjual_PenjualId(penjual.getPenjualId());

        model.addAttribute("penjual", penjual);
        model.addAttribute("produkList", produkList);

        return "penjual/profil";
    }
}
