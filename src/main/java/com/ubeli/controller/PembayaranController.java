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

    // TAMPILKAN HALAMAN PEMBAYARAN (Dari Link Notifikasi)
    @GetMapping("/pesanan/{id}/lanjut")
    public String halamanPembayaran(@PathVariable Long id, Model model, HttpSession session) {
        
        if (session.getAttribute("role") == null) {
            System.out.println("ISINYA USER ATAU EMANG NULL");
            return "redirect:/login";
        }

        Pesanan pesanan = pesananRepo.findById(id).orElse(null);

        if (pesanan != null) {
            model.addAttribute("pesanan", pesanan);

            if (!pesanan.getItems().isEmpty()) {
                model.addAttribute("produk", pesanan.getItems().get(0).getProduk());
            }
            
            return "pembeli/checkout"; 
        }

        return "redirect:/"; 
    }

    // PROSES UPLOAD BUKTI TRANSFER
    @PostMapping("/transaksi/bayar")
    public String prosesUploadBukti(@RequestParam Long pesananId,
                                    @RequestParam("fileBukti") MultipartFile fileBukti) {
        
        Pesanan pesanan = pesananRepo.findById(pesananId).orElse(null);
        
        if (pesanan != null && !fileBukti.isEmpty()) {
            try {
                String namaFile = UUID.randomUUID() + "_" + fileBukti.getOriginalFilename();
                
                Path uploadDir = Paths.get("src/main/resources/static/img/bukti/");
                if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
                
                Files.copy(fileBukti.getInputStream(), uploadDir.resolve(namaFile), StandardCopyOption.REPLACE_EXISTING);
                
                pesanan.setBuktiTransferUrl("/img/bukti/" + namaFile);
                pesanan.setStatusPesanan(StatusPesanan.VERIFIKASI_ADMIN); // Status berubah jadi 'Menunggu Verifikasi'
                
                pesananRepo.save(pesanan);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return "redirect:/riwayat-pesanan";
    }   
}