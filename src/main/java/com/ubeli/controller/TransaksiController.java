package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import com.ubeli.entity.*;
import com.ubeli.enums.StatusPesanan;
import com.ubeli.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List; // Jangan lupa import List

@Controller
public class TransaksiController {

    @Autowired private ProdukRepository produkRepo;
    @Autowired private PesananRepository pesananRepo;
    @Autowired private ItemRepository itemRepo;
    @Autowired private NotifikasiRepository notifRepo;

    // ========================================================
    // 1. USE CASE: CHECKOUT / AJUKAN PEMBELIAN
    // ========================================================
    @PostMapping("/transaksi/proses")
    public String prosesCheckout(@RequestParam Long produkId, 
                                 @RequestParam int jumlah,
                                 @RequestParam String catatan,
                                 HttpSession session) {

        // 1. Cek User
        Object userObj = session.getAttribute("user");
        if (userObj == null || !"PEMBELI".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }
        Pembeli pembeli = (Pembeli) userObj;

        // 2. Ambil Produk
        Produk produk = produkRepo.findById(produkId).orElse(null);
        if (produk == null) {
            return "redirect:/katalog?error=produk-tidak-ditemukan";
        }

        // 3. Buat Pesanan
        Pesanan pesanan = new Pesanan();
        pesanan.setPembeli(pembeli);
        pesanan.setPenjual(produk.getPenjual());
        // Status awal: Menunggu Konfirmasi Penjual
        pesanan.setStatusPesanan(StatusPesanan.MENUNGGU_KONFIRMASI_PENJUAL);
        
        // Jika ada field catatan di Pesanan, set disini
        // pesanan.setCatatanPembeli(catatan); 

        BigDecimal hargaProduk = produk.getHarga();
        BigDecimal totalHarga = hargaProduk.multiply(new BigDecimal(jumlah));
        pesanan.setTotalHarga(totalHarga);
        
        pesanan = pesananRepo.save(pesanan);

        // 4. Buat Item
        Item item = new Item();
        item.setPesanan(pesanan);
        item.setProduk(produk);
        item.setJumlah(jumlah);
        item.setHargaSatuanSaatIni(hargaProduk);
        item.setSubtotal(totalHarga);
        itemRepo.save(item);

        // 5. Notifikasi Penjual
        Notifikasi notifPenjual = new Notifikasi();
        notifPenjual.setJudul("PESANAN BARU MASUK 📦");
        notifPenjual.setSubJudul("Cek pesanan untuk: " + produk.getNamaProduk());
        notifPenjual.setWaktu(LocalDateTime.now());
        notifPenjual.setStatus("MENUNGGU"); 
        notifPenjual.setPenjual(produk.getPenjual());
        notifPenjual.setPesanan(pesanan);
        notifRepo.save(notifPenjual);

        // 6. Notifikasi Pembeli
        Notifikasi notifPembeli = new Notifikasi();
        notifPembeli.setJudul("Pengajuan Pembelian Berhasil!");
        notifPembeli.setSubJudul("Menunggu konfirmasi penjual: " + produk.getNamaProduk());
        notifPembeli.setWaktu(LocalDateTime.now());
        notifPembeli.setStatus("MENUNGGU"); 
        notifPembeli.setPembeli(pembeli);
        notifPembeli.setPesanan(pesanan);
        notifRepo.save(notifPembeli);

        return "redirect:/riwayat-pesanan";
    }

    // ========================================================
    // 2. USE CASE: TAMPILKAN HALAMAN RIWAYAT (YANG KAMU MINTA)
    // URL: /riwayat-pesanan
    // ========================================================
    @GetMapping("/riwayat-pesanan")
    public String halamanRiwayat(Model model, HttpSession session) {
        
        // 1. AMBIL DATA USER DARI SESSION
        // Coba ambil dengan key "user" dulu (standar kode kita sebelumnya)
        Object userObj = session.getAttribute("user");
        
        // Jika null, coba ambil dengan key "pembeli" (siapa tau AuthController nyimpennya pakai nama ini)
        if (userObj == null) {
            userObj = session.getAttribute("pembeli");
        }

        // 2. CEK APAKAH BENAR-BENAR LOGIN
        if (userObj == null) {
            System.out.println("Session kosong! Redirect ke login.");
            return "redirect:/login";
        }

        // 3. CASTING KE TIPE DATA PEMBELI
        // Karena kita yakin ini halaman pembeli, kita ubah Object jadi Pembeli
        Pembeli pembeli = (Pembeli) userObj;

        // 4. LOGIKA AMBIL DATA
        List<Pesanan> listPesanan = pesananRepo.findByPembeli_PembeliIdOrderByPesananIdDesc(pembeli.getPembeliId());
        
        model.addAttribute("listPesanan", listPesanan);
        model.addAttribute("pembeli", pembeli); // Kirim data pembeli buat jaga-jaga (avatar/nama di navbar)

        return "pembeli/riwayat-pesanan"; 
    }

    // ========================================================
    // 3. USE CASE: KONFIRMASI TERIMA BARANG (UPDATE STATUS)
    // ========================================================
    @PostMapping("/transaksi/konfirmasi-terima")
    public String konfirmasiTerima(@RequestParam Long pesananId) {
        
        Pesanan pesanan = pesananRepo.findById(pesananId).orElse(null);

        if (pesanan != null) {
            // Ubah Status jadi SELESAI
            pesanan.setStatusPesanan(StatusPesanan.SELESAI);
            pesananRepo.save(pesanan);

            System.out.println("Pesanan #" + pesananId + " telah SELESAI. Dana diteruskan ke Penjual.");
            
            // (Opsional) Di sini bisa tambah notifikasi ke Penjual "Dana Cair"
        }

        return "redirect:/riwayat-pesanan";
    }
}