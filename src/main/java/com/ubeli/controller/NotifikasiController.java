package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

import com.ubeli.repository.NotifikasiRepository;
import com.ubeli.repository.PesananRepository;
import com.ubeli.entity.Notifikasi;
import com.ubeli.entity.Pesanan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class NotifikasiController {

    private final NotifikasiRepository notifRepo;
    private final PesananRepository pesananRepo;

    public NotifikasiController(NotifikasiRepository notifRepo,
                                PesananRepository pesananRepo) {
        this.notifRepo = notifRepo;
        this.pesananRepo = pesananRepo;
    }

    @GetMapping("/notifikasi")
    public String notifPage(Model model, HttpSession session) {

        String role = (String) session.getAttribute("role");
        Long userId = null;

        if ("PEMBELI".equals(role)) {
            userId = (Long) session.getAttribute("pembeliId");
        } else if ("PENJUAL".equals(role)) {
            userId = (Long) session.getAttribute("penjualId");
            // some code earlier used "userId" generic key — fallback:
            if (userId == null) userId = (Long) session.getAttribute("userId");
        } else {
            // fallback generic
            userId = (Long) session.getAttribute("userId");
        }

        if (role == null || userId == null) {
            return "redirect:/login";
        }

        model.addAttribute("role", role);

        // PEMBELI → Notifikasi Pembeli
        if (role.equals("PEMBELI")) {
            model.addAttribute("listNotif",
                notifRepo.findByPembeli_PembeliId(userId));
            return "pembeli/notifikasi-pembeli";
        }

        // PENJUAL → Daftar Pesanan + Notifikasi (map)
        if (role.equals("PENJUAL")) {

            List<Pesanan> listPesanan = pesananRepo.findByPenjual_PenjualId(userId);

            Map<Long, Notifikasi> mapNotif = new HashMap<>();

            for (Pesanan p : listPesanan) {
                notifRepo.findFirstByPesanan_PesananIdOrderByIdDesc(p.getPesananId())
                        .ifPresent(n -> mapNotif.put(p.getPesananId(), n));
            }

            model.addAttribute("listPesanan", listPesanan);
            model.addAttribute("mapNotif", mapNotif);
            return "penjual/notifikasi-penjual";
        }

        return "redirect:/home";
    }
}
