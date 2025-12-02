package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
// import com.ubeli.repository.LaporanRepository;

@Controller
public class BuatLaporanController {

    // @Autowired
    // private LaporanRepository laporanRepository;

    // Method untuk Pembeli mengirim laporan
    public void kirimLaporan(String alasan, String buktiUrl, Long produkId) {
        // Logika simpan laporan baru ke database
        // Status awal otomatis "Pending"
        System.out.println("Laporan berhasil dikirim: " + alasan);
    }
}