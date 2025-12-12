package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;

import com.ubeli.entity.Pesanan;
import com.ubeli.repository.PesananRepository;
import com.ubeli.enums.StatusPesanan;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Controller
public class PembayaranController {

    @Autowired
    private PesananRepository pesananRepo;

    // ============================================================
    // 1. TAMPILKAN HALAMAN PEMBAYARAN (Dari Link Notifikasi)
    // URL: /pesanan/{id}/lanjut
    // ============================================================
    @GetMapping("/pesanan/{id}/lanjut")
    public String halamanPembayaran(@PathVariable Long id, Model model, HttpSession session) {
        
        // Cek Login
        if (session.getAttribute("role") == null) {
            System.out.println("ISINYA USER ATAU EMANG NULL");
            return "redirect:/login";
        }

        // Cari Pesanan
        Pesanan pesanan = pesananRepo.findById(id).orElse(null);

        if (pesanan != null) {
            // Masukkan data pesanan ke HTML
            model.addAttribute("pesanan", pesanan);

            // Masukkan data produk (diambil dari item pertama)
            // HTML pembayaran butuh variabel ${produk.namaProduk}
            if (!pesanan.getItems().isEmpty()) {
                model.addAttribute("produk", pesanan.getItems().get(0).getProduk());
            }
            
            // Buka file HTML pembayaran
            return "pembeli/checkout"; 
        }

        return "redirect:/"; // Kalau ID salah/gak ketemu
    }

    // ============================================================
    // 2. PROSES UPLOAD BUKTI TRANSFER
    // URL: /transaksi/bayar (Sesuai action di form HTML)
    // ============================================================
    @PostMapping("/transaksi/bayar")
    public String prosesUploadBukti(@RequestParam Long pesananId,
                                    @RequestParam("fileBukti") MultipartFile fileBukti) {
        
        Pesanan pesanan = pesananRepo.findById(pesananId).orElse(null);
        
        if (pesanan != null && !fileBukti.isEmpty()) {
            try {
                // 1. Generate Nama File Unik
                String namaFile = UUID.randomUUID() + "_" + fileBukti.getOriginalFilename();
                
                // 2. Tentukan Folder Simpan
                Path uploadDir = Paths.get("src/main/resources/static/img/bukti/");
                if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
                
                // 3. Simpan File Fisik
                Files.copy(fileBukti.getInputStream(), uploadDir.resolve(namaFile), StandardCopyOption.REPLACE_EXISTING);
                
                // 4. Update Database (Path Gambar & Status)
                pesanan.setBuktiTransferUrl("/img/bukti/" + namaFile);
                pesanan.setStatusPesanan(StatusPesanan.VERIFIKASI_ADMIN); // Status berubah jadi 'Menunggu Verifikasi'
                
                pesananRepo.save(pesanan);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // Balik ke riwayat pesanan
        return "redirect:/riwayat-pesanan";
    }
}