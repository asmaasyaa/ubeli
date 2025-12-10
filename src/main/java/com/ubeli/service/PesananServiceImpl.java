package com.ubeli.service;

import com.ubeli.entity.Pesanan;
import com.ubeli.entity.Produk;
import com.ubeli.entity.Pembeli;
import com.ubeli.enums.StatusPengajuan;
import com.ubeli.repository.PesananRepository;
import com.ubeli.repository.ProdukRepository;
import com.ubeli.repository.PembeliRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class PesananServiceImpl implements com.ubeli.service.PesananService {

    private final PesananRepository pesananRepo;
    private final ProdukRepository produkRepo;
    private final PembeliRepository pembeliRepo;

    // ========================================
    // 1. AJUKAN PEMBELIAN
    // ========================================
    @Override
    @Transactional
    public Pesanan ajukanPembelian(Long produkId, Long pembeliId) {

        // Cek produk tersedia
        Produk produk = produkRepo.findById(produkId)
                .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));

        if (!produk.getStatus().equalsIgnoreCase("Available")) {
            throw new RuntimeException("Produk sudah tidak tersedia / sudah dikunci");
        }

        // Cek pembeli
        Pembeli pembeli = pembeliRepo.findById(pembeliId)
                .orElseThrow(() -> new RuntimeException("Pembeli tidak ditemukan"));

        // Buat pengajuan baru
        Pesanan pesanan = new Pesanan();
        pesanan.setProduk(produk);
        pesanan.setPembeli(pembeli);
        pesanan.setPenjual(produk.getPenjual());
        pesanan.setStatusPengajuan(StatusPengajuan.PENDING);
        pesanan.setStatusPesanan(null); // belum transaksi

        return pesananRepo.save(pesanan);
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

        // Tolak semua pengajuan lain
        pesananRepo.updateStatusPengajuanForOthers(
                produk.getProdukId(),
                pesananId,
                StatusPengajuan.DITOLAK
        );

        // Kunci produk agar tidak muncul di katalog
        produk.setStatus("Locked"); // atau 2 sesuai desainmu
        produkRepo.save(produk);

        return pesanan;
    }
}
