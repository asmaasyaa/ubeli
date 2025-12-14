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
        Long userId = (Long) session.getAttribute("userId");

        if (role == null) return "redirect:/login";

        model.addAttribute("role", role);

        // ======================
        // PEMBELI → Notifikasi Pembeli
        // ======================
        if (role.equals("PEMBELI")) {
            model.addAttribute("listNotif",
                notifRepo.findByPembeli_PembeliId(userId));

            return "pembeli/notifikasi-pembeli";
        }

        // ======================
        // PENJUAL → Daftar Pesanan + Notifikasi
        // ======================
        if (role.equals("PENJUAL")) {

            List<Pesanan> listPesanan =
                    pesananRepo.findByPenjual_PenjualId(userId);

            // Buat map: pesananId → notifikasi
            Map<Long, Notifikasi> mapNotif = new HashMap<>();

            for (Pesanan p : listPesanan) {
                Notifikasi notif =
                        notifRepo.findFirstByPesanan_PesananIdOrderByIdDesc(p.getPesananId());
                mapNotif.put(p.getPesananId(), notif);
            }

            model.addAttribute("listPesanan", listPesanan);
            model.addAttribute("mapNotif", mapNotif);

            return "penjual/notifikasi-penjual";
        }

        return "redirect:/home";
    }
}
