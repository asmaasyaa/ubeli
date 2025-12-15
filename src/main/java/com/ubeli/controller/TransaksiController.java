package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import com.ubeli.entity.Pembeli;
import com.ubeli.entity.Pesanan;
import com.ubeli.entity.Notifikasi;
import com.ubeli.enums.StatusPesanan;
import com.ubeli.repository.PesananRepository;
import com.ubeli.repository.NotifikasiRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TransaksiController {

    private final PesananRepository pesananRepo;
    private final NotifikasiRepository notifikasiRepo; 

    // ========================================================
    // 1. USE CASE: TAMPILKAN HALAMAN RIWAYAT
    // URL: /riwayat-pesanan
    // ========================================================
    @GetMapping("/riwayat-pesanan")
    public String halamanRiwayat(Model model, HttpSession session) {
        
        Object userObj = session.getAttribute("user");
        if (userObj == null) userObj = session.getAttribute("pembeli");

        if (userObj == null) return "redirect:/login";
        if (!(userObj instanceof Pembeli)) return "redirect:/home"; 

        Pembeli pembeli = (Pembeli) userObj;

        List<Pesanan> listPesanan = pesananRepo.findByPembeli_PembeliIdOrderByPesananIdDesc(pembeli.getPembeliId());
        
        model.addAttribute("listPesanan", listPesanan);
        model.addAttribute("pembeli", pembeli); 

        return "pembeli/riwayat-pesanan"; 
    }

    // ========================================================
    // 2. [PINDAHAN] USE CASE: TAMPILKAN HALAMAN CHECKOUT
    // URL: /pesanan/{id}/lanjut (Diklik dari Notifikasi/Riwayat)
    // ========================================================
    @GetMapping("/pesanan/{id}/lanjut")
    public String halamanPembayaran(@PathVariable Long id, Model model, HttpSession session) {
        
        // Cek Login
        if (session.getAttribute("user") == null && session.getAttribute("pembeli") == null) {
            return "redirect:/login";
        }

        Pesanan pesanan = pesananRepo.findById(id).orElse(null);

        if (pesanan != null) {
            model.addAttribute("pesanan", pesanan);

            // Ambil data produk untuk ditampilkan di checkout
            if (pesanan.getItems() != null && !pesanan.getItems().isEmpty()) {
                model.addAttribute("produk", pesanan.getItems().get(0).getProduk());
            }
            
            return "pembeli/checkout"; 
        }

        return "redirect:/riwayat-pesanan";
    }

    // ========================================================
    // 3. USE CASE: PROSES BAYAR / UPLOAD BUKTI (POST)
    // URL: /transaksi/bayar (Action dari Form Checkout)
    // ========================================================
    @PostMapping("/transaksi/bayar")
    public String prosesBayar(
            @RequestParam Long pesananId,
            @RequestParam String metodePembayaran, 
            @RequestParam(value = "fileBukti", required = false) MultipartFile fileBukti,
            HttpSession session
    ) {
        
        if (session.getAttribute("user") == null && session.getAttribute("pembeli") == null) {
            return "redirect:/login";
        }

        Pesanan pesanan = pesananRepo.findById(pesananId).orElse(null);
        if (pesanan == null) return "redirect:/riwayat-pesanan";

        // Simpan Metode
        pesanan.setMetodePembayaran(metodePembayaran);

        // Logic Status
        if ("COD".equals(metodePembayaran)) {
            pesanan.setStatusPesanan(StatusPesanan.VERIFIKASI_ADMIN); 
        } else {
            // Logic File Upload (Simpel)
            if (fileBukti != null && !fileBukti.isEmpty()) {
                String namaFile = fileBukti.getOriginalFilename();
                pesanan.setBuktiTransferUrl("/img/bukti/" + namaFile); 
                // Note: Logic save file fisik bisa ditambahkan jika perlu
            }
            pesanan.setStatusPesanan(StatusPesanan.VERIFIKASI_ADMIN);
        }

        pesananRepo.save(pesanan);

        return "redirect:/riwayat-pesanan?success=dibayar";
    }

    // ========================================================
    // 4. USE CASE: KONFIRMASI TERIMA BARANG
    // ========================================================
    @PostMapping("/transaksi/kirim-barang")
    public String konfirmasiKirim(@RequestParam Long pesananId, HttpSession session) {
        
        // Cek Login Penjual
        // Note: Logic login check might need adjustment depending on how you store seller session
        // Assuming "penjual" or checking role
        if (session.getAttribute("penjual") == null && !"PENJUAL".equals(session.getAttribute("role"))) {
             return "redirect:/login";
        }

        Pesanan p = pesananRepo.findById(pesananId).orElse(null);
        if (p != null && p.getStatusPesanan() == StatusPesanan.DIBAYAR) {
            
            // 1. Ubah Status
            p.setStatusPesanan(StatusPesanan.DIKIRIM);
            pesananRepo.save(p);

            // 2. Notifikasi ke Pembeli
            Notifikasi notif = new Notifikasi();
            notif.setJudul("Pesanan Sedang Dikirim 🚚");
            
            // Ambil nama produk dengan aman
            String namaProduk = "Barang";
            if (p.getProduk() != null) {
                namaProduk = p.getProduk().getNamaProduk();
            } else if (p.getItems() != null && !p.getItems().isEmpty()) {
                 if (p.getItems().get(0).getProduk() != null) {
                    namaProduk = p.getItems().get(0).getProduk().getNamaProduk();
                 }
            }

            notif.setSubJudul("Paket berisi " + namaProduk + " sedang dalam perjalanan.");
            notif.setWaktu(LocalDateTime.now());
            notif.setStatus("DIKIRIM");
            notif.setPesanan(p);
            notif.setPembeli(p.getPembeli()); // Kirim ke Pembeli
            
            notifikasiRepo.save(notif);
        }
        return "redirect:/notifikasi"; // Kembali ke halaman notifikasi penjual
    }

    // ========================================================
    // 5. USE CASE: KONFIRMASI TERIMA BARANG (UPDATED)
    // ========================================================
    @PostMapping("/transaksi/konfirmasi-terima")
    public String konfirmasiTerima(@RequestParam Long pesananId, HttpSession session) {
        
        if (session.getAttribute("user") == null && session.getAttribute("pembeli") == null) {
            return "redirect:/login";
        }

        Pesanan p = pesananRepo.findById(pesananId).orElse(null);

        if (p != null && p.getStatusPesanan() == StatusPesanan.DIKIRIM) { 
            // 1. Ubah Status jadi SELESAI
            p.setStatusPesanan(StatusPesanan.SELESAI);
            pesananRepo.save(p);

            // 2. Notifikasi ke PENJUAL (Info Saldo)
            Notifikasi notifPenjual = new Notifikasi();
            notifPenjual.setJudul("Transaksi Selesai 🎉");
            notifPenjual.setSubJudul("Pembeli telah menerima barang. Saldo akan diteruskan ke rekening Anda.");
            notifPenjual.setWaktu(LocalDateTime.now());
            notifPenjual.setStatus("SELESAI");
            notifPenjual.setPesanan(p);
            notifPenjual.setPenjual(p.getPenjual());
            notifikasiRepo.save(notifPenjual);

            // 3. (BARU) Notifikasi ke PEMBELI (Konfirmasi Sukses)
            Notifikasi notifPembeli = new Notifikasi();
            notifPembeli.setJudul("Pesanan Diterima ✅");
            notifPembeli.setSubJudul("Terima kasih telah berbelanja! Transaksi selesai.");
            notifPembeli.setWaktu(LocalDateTime.now());
            notifPembeli.setStatus("SELESAI");
            notifPembeli.setPesanan(p);
            notifPembeli.setPembeli(p.getPembeli()); // Kirim ke Pembeli juga
            notifikasiRepo.save(notifPembeli);
        }

        return "redirect:/riwayat-pesanan";
    }
}