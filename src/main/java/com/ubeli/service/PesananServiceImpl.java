package com.ubeli.service;

import com.ubeli.entity.Pesanan;
import com.ubeli.entity.Produk;
import com.ubeli.entity.Pembeli;
import com.ubeli.enums.StatusPengajuan;
import com.ubeli.repository.PesananRepository;
import com.ubeli.repository.ProdukRepository;
import com.ubeli.repository.NotifikasiRepository;
import com.ubeli.repository.PembeliRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;

import com.ubeli.repository.NotifikasiRepository;
import com.ubeli.entity.Notifikasi;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class PesananServiceImpl implements com.ubeli.service.PesananService {

    private final PesananRepository pesananRepo;
    private final ProdukRepository produkRepo;
    private final PembeliRepository pembeliRepo;
    private final NotifikasiRepository notifRepo;


    // ========================================
    // 1. AJUKAN PEMBELIAN
    // ========================================
    @Override
    @Transactional
    public Pesanan ajukanPembelian(Long produkId, Long pembeliId) {

        Produk produk = produkRepo.findById(produkId)
                .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));

        if (!produk.getStatus().equalsIgnoreCase("Available")) {
            throw new RuntimeException("Produk sudah tidak tersedia");
        }

        Pembeli pembeli = pembeliRepo.findById(pembeliId)
                .orElseThrow(() -> new RuntimeException("Pembeli tidak ditemukan"));

        Pesanan pesanan = new Pesanan();
        pesanan.setProduk(produk);
        pesanan.setPembeli(pembeli);
        pesanan.setPenjual(produk.getPenjual());
        pesanan.setStatusPengajuan(StatusPengajuan.PENDING);
        pesanan.setStatusPesanan(null);
        pesanan.setTotalHarga(produk.getHarga());

        Pesanan saved = pesananRepo.save(pesanan);

        // =========================================
        // NOTIFIKASI UNTUK PENJUAL
        // =========================================
        Notifikasi notif = new Notifikasi();
        notif.setJudul(pembeli.getNamaLengkap() + " mengajukan pembelian");
        notif.setSubJudul(produk.getNamaProduk());
        notif.setStatus("MENUNGGU");
        notif.setWaktu(LocalDateTime.now().toString());
        notif.setPenjual(produk.getPenjual());
        notif.setPembeli(null);
        notif.setPesanan(saved); 
        notifRepo.save(notif);

        return saved;
    }

    // ========================================
    // 2. PENJUAL MENERIMA SALAH SATU PENGAJUAN
    // ========================================
    @Override
    @Transactional
    public Pesanan terimaPengajuan(Long pesananId) {

        Pesanan pesanan = pesananRepo.findById(pesananId)
                .orElseThrow(() -> new RuntimeException("Pesanan tidak ditemukan"));

        Produk produk = pesanan.getProduk();

        // Terima pengajuan ini
        pesanan.setStatusPengajuan(StatusPengajuan.DITERIMA);
        pesananRepo.save(pesanan);

        // Tolak pengajuan lain
        pesananRepo.updateStatusPengajuanForOthers(
                produk.getProdukId(),
                pesananId,
                StatusPengajuan.DITOLAK
        );

        // Kunci produk
        produk.setStatus("Locked");
        produkRepo.save(produk);

        // =========================================
        // NOTIFIKASI UNTUK PEMBELI YANG DITERIMA
        // =========================================
        Notifikasi notif = new Notifikasi();
        notif.setJudul("Pengajuan Anda diterima");
        notif.setSubJudul(pesanan.getProduk().getNamaProduk());
        notif.setStatus("DITERIMA");
        notif.setWaktu(LocalDateTime.now().toString());
        notif.setPembeli(pesanan.getPembeli());
        notif.setPenjual(null);
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

        // NOTIFIKASI UNTUK PEMBELI
        Notifikasi notif = new Notifikasi();
        notif.setJudul("Pengajuan Anda ditolak");
        notif.setSubJudul(pesanan.getProduk().getNamaProduk());
        notif.setWaktu(LocalDateTime.now().toString());
        notif.setStatus("DITOLAK");
        notif.setPembeli(pesanan.getPembeli());

        notifRepo.save(notif);
    }

}
