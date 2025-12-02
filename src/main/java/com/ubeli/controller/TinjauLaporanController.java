package com.ubeli.controller;

import org.springframework.stereotype.Controller;
// import com.ubeli.repository.LaporanRepository;

@Controller
public class TinjauLaporanController {

    // Method untuk Admin merespon laporan
    public void updateStatusLaporan(Long laporanId, String keputusan) {
        // Logika update status (misal jadi "Selesai" atau "Ditolak")
        System.out.println("Admin mengubah status laporan " + laporanId + " menjadi: " + keputusan);
    }
}