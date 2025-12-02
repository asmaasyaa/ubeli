package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ubeli.entity.BannerIklan;
import com.ubeli.enums.StatusIklan;
//import com.ubeli.repository.BannerIklanRepository; // Pastikan bikin repo-nya ya!

import java.time.LocalDate;

@Controller
public class ValidasiIklanController {

    // @Autowired
    // private BannerIklanRepository bannerIklanRepository;

    // Contoh Method: Admin Mengaktifkan Iklan
    @PostMapping("/admin/iklan/aktifkan/{id}")
    public String aktifkanIklan(@PathVariable Long id) {
        
        // 1. Cari Iklan di Database
        // BannerIklan iklan = bannerIklanRepository.findById(id).orElse(null);

        // if (iklan != null) {
        //     // 2. Ubah Status jadi ACTIVE
        //     iklan.setStatus(StatusIklan.ACTIVE);
            
        //     // 3. Set Tanggal Mulai (Hari ini)
        //     iklan.setTanggalMulai(LocalDate.now());

        //     // 4. Hitung Tanggal Selesai (Berdasarkan Enum Paket)
        //     int durasi = iklan.getJenisPaket().getDurasiHari();
        //     iklan.setTanggalSelesai(LocalDate.now().plusDays(durasi));

        //     // 5. Simpan Perubahan
        //     bannerIklanRepository.save(iklan);
        //}

        return "redirect:/admin/dashboard";
    }
}