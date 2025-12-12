package com.ubeli.controller.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ubeli.entity.Penjual;
import com.ubeli.repository.PenjualRepository;

@Controller
public class PenjualPageController {

    @Autowired
    private PenjualRepository penjualRepo;

    // ========== HALAMAN PROFIL TOKO ==========
    @GetMapping("/penjual/profil-toko")
    public String profilToko(Model model) {

        // sementara pakai dummy penjual id = 1
        Penjual penjual = penjualRepo.findById(1L).orElse(null);

        model.addAttribute("penjual", penjual);

        return "penjual/profil";  
    }

    

}
