package com.ubeli.service;

import com.ubeli.entity.*;
import com.ubeli.enums.StatusPengajuan;
import com.ubeli.enums.StatusPesanan;
import com.ubeli.repository.*;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.Optional; // Digunakan untuk findById

@Service
@RequiredArgsConstructor
public class PesananServiceImpl implements com.ubeli.service.PesananService {

    // Dependency Injection (RequiredArgsConstructor menangani ini)
    private final PesananRepository pesananRepo;
    private final ProdukRepository produkRepo;
    private final PembeliRepository pembeliRepo;
    private final NotifikasiRepository notifRepo;
    private final ItemRepository itemRepo; // Pastikan ini juga di-inject

    // ========================================
    // 1. AJUKAN PEMBELIAN (CREATE NEW PESANAN)
    // ========================================
    @Override
    @Transactional
    public Pesanan ajukanPembelian(Long produkId, Long pembeliId) {

        Produk produk = produkRepo.findById(produkId)
                .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));

        if (!"Available".equalsIgnoreCase(produk.getStatus())) {
            throw new RuntimeException("Produk sudah tidak tersedia");
        }

        Pembeli pembeli = pembeliRepo.findById(pembeliId)
                .orElseThrow(() -> new RuntimeException("Pembeli tidak ditemukan"));

        // 1. BUAT PESANAN UTAMA
        Pesanan pesanan = new Pesanan();
        // ASUMSI: Di entity Pesanan kamu punya field setProduk, setPembeli, dll.
        // Jika tidak ada setProduk di Pesanan, hapus baris ini:
        // pesanan.setProduk(produk); 
        
        pesanan.setPembeli(pembeli);
        pesanan.setPenjual(produk.getPenjual());
        pesanan.setStatusPengajuan(StatusPengajuan.PENDING);
        pesanan.setStatusPesanan(null); // Status Pesanan diisi nanti (saat bayar)
        pesanan.setTotalHarga(produk.getHarga());

        Pesanan savedPesanan = pesananRepo.save(pesanan);

        // 2. BUAT ITEM (KUNCI AGAR DATA PRODUK TERIKAT DENGAN PESANAN)
        Item item = new Item();
        item.setPesanan(savedPesanan); 
        item.setProduk(produk);
        item.setJumlah(1); 
        item.setHargaSatuanSaatIni(produk.getHarga());
        item.setSubtotal(produk.getHarga());
        itemRepo.save(item);

        // 3. NOTIFIKASI UNTUK PENJUAL
        Notifikasi notifPenjual = new Notifikasi();
        notifPenjual.setJudul(pembeli.getNamaLengkap() + " mengajukan pembelian");
        notifPenjual.setSubJudul(produk.getNamaProduk());
        notifPenjual.setStatus("MENUNGGU");
        notifPenjual.setWaktu(LocalDateTime.now()); // FIX: Menggunakan LocalDateTime
        notifPenjual.setPenjual(produk.getPenjual());
        notifPenjual.setPesanan(savedPesanan); 
        notifRepo.save(notifPenjual);

        // 4. NOTIFIKASI UNTUK PEMBELI
        Notifikasi notifPembeli = new Notifikasi();
        notifPembeli.setJudul("Pengajuan Pembelian Berhasil");
        notifPembeli.setSubJudul("Menunggu konfirmasi penjual: " + produk.getNamaProduk());
        notifPembeli.setStatus("MENUNGGU");
        notifPembeli.setWaktu(LocalDateTime.now());
        notifPembeli.setPembeli(pembeli);
        notifPembeli.setPesanan(savedPesanan); 
        notifRepo.save(notifPembeli);

        return savedPesanan;
    }

    // ========================================
    // 2. PENJUAL MENERIMA SALAH SATU PENGAJUAN
    // ========================================
    @Override
    @Transactional
    public Pesanan terimaPengajuan(Long pesananId) {

        Pesanan pesanan = pesananRepo.findById(pesananId)
                .orElseThrow(() -> new RuntimeException("Pesanan tidak ditemukan"));

        // Ambil produk dari item pertama (Asumsi 1 Item per Pesanan)
        Produk produk = pesanan.getItems().get(0).getProduk(); 

        // Terima pengajuan ini
        pesanan.setStatusPengajuan(StatusPengajuan.DITERIMA);
        // *SET STATUS PESANAN AWAL AGAR PEMBELI TAHU HARUS BAYAR*
        pesanan.setStatusPesanan(StatusPesanan.MENUNGGU_PEMBAYARAN); 
        pesananRepo.save(pesanan);

        // Kunci produk
        produk.setStatus("Locked");
        produkRepo.save(produk);

        // 3. NOTIFIKASI UNTUK PEMBELI YANG DITERIMA
        Notifikasi notif = new Notifikasi();
        notif.setJudul("Pengajuan Anda diterima");
        notif.setSubJudul("Lanjutkan pembayaran untuk: " + produk.getNamaProduk());
        notif.setStatus("DITERIMA");
        notif.setWaktu(LocalDateTime.now());
        notif.setPembeli(pesanan.getPembeli());
        notif.setPesanan(pesanan);
        notifRepo.save(notif);

        return pesanan;
    }


    @Override
    @Transactional
    public void tolakPengajuan(Long pesananId) {

        Pesanan pesanan = pesananRepo.findById(pesananId)
                .orElseThrow(() -> new RuntimeException("Pesanan tidak ditemukan"));

        pesanan.setStatusPengajuan(StatusPengajuan.DITOLAK);
        pesananRepo.save(pesanan);
        
        // Produk dikembalikan statusnya (tidak dibahas di sini)

        // NOTIFIKASI UNTUK PEMBELI
        Notifikasi notif = new Notifikasi();
        notif.setJudul("Pengajuan Anda ditolak");
        notif.setSubJudul(pesanan.getProduk().getNamaProduk());
        notif.setWaktu(LocalDateTime.now());
        notif.setStatus("DITOLAK");
        notif.setPembeli(pesanan.getPembeli());
        notifRepo.save(notif);
    }
}