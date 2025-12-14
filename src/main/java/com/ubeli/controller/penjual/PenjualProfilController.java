package com.ubeli.controller.penjual;

import com.ubeli.entity.Penjual;
import com.ubeli.entity.Produk;
import com.ubeli.repository.*;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class PenjualProfilController {

    @Autowired
    private ProdukRepository produkRepo;

    @Autowired
    private PenjualRepository penjualRepo;

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
    
    // HALAMAN EDIT PROFIL 
    @GetMapping("/penjual/edit-profil")
    public String editProfil(HttpSession session, Model model) {

        // CEK ROLE
        if (!"PENJUAL".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        // CEK PENJUAL DALAM SESSION
        Penjual penjual = (Penjual) session.getAttribute("penjual");

        if (penjual == null) {
            session.invalidate();
            return "redirect:/login";
        }

        model.addAttribute("penjual", penjual);

        return "penjual/edit-profil"; 
    }

    @PostMapping("/penjual/update-profil")
    public String updateProfil(
            @RequestParam String namaLengkap,
            @RequestParam String email,
            @RequestParam String noHp,
            @RequestParam(required = false) String deskripsi,
            @RequestParam(required = false) String lokasiToko,
            HttpSession session,
            Model model
    ) {

        // CEK ROLE
        if (!"PENJUAL".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        // AMBIL PENJUAL DARI SESSION
        Penjual penjual = (Penjual) session.getAttribute("penjual");

        if (penjual == null) {
            session.invalidate();
            return "redirect:/login";
        }

        // UPDATE FIELD
        penjual.setNamaLengkap(namaLengkap);
        penjual.setEmail(email);
        penjual.setNoHp(noHp);
        penjual.setDeskripsiToko(deskripsi); 
        penjual.setLokasiToko(lokasiToko);

        // SIMPAN KE DATABASE
        penjualRepo.save(penjual); 

        // UPDATE DATA DI SESSION AGAR PERUBAHAN LANGSUNG TERLIHAT
        session.setAttribute("penjual", penjual);

        return "redirect:/penjual/profil?success";
    }

}
