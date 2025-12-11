package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

import com.ubeli.entity.Notifikasi;
import com.ubeli.repository.NotifikasiRepository;

import java.util.List;

@Controller
public class NotifikasiController {

    private final NotifikasiRepository notifRepo;

    // Constructor injection manual (biar tidak pakai Lombok)
    public NotifikasiController(NotifikasiRepository notifRepo) {
        this.notifRepo = notifRepo;
    }

    @GetMapping("/notifikasi")
    public String notifPage(Model model, HttpSession session) {

        String role = (String) session.getAttribute("role");
        Long userId = (Long) session.getAttribute("userId");

        if (role == null) return "redirect:/login";

        model.addAttribute("role", role);

        if (role.equals("PEMBELI")) {
            model.addAttribute("listNotif",
                notifRepo.findByPembeli_PembeliId(userId));
            return "pembeli/notifikasi-pembeli";
        }

        if (role.equals("PENJUAL")) {
            model.addAttribute("listNotif",
                notifRepo.findByPenjual_PenjualId(userId));
            return "penjual/notifikasi-penjual";
        }

        return "redirect:/home";
    }


}
