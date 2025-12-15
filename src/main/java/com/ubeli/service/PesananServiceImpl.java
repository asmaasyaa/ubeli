package com.ubeli.service;

import com.ubeli.entity.Pesanan;
import com.ubeli.entity.Produk;
import com.ubeli.entity.Pembeli;
import com.ubeli.entity.Item;
import com.ubeli.entity.Notifikasi;
import com.ubeli.enums.StatusPengajuan;
import com.ubeli.enums.StatusPesanan;
import com.ubeli.repository.*;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PesananServiceImpl implements PesananService {

    private final PesananRepository pesananRepo;
    private final ProdukRepository produkRepo;
    private final PembeliRepository pembeliRepo;
    private final NotifikasiRepository notifRepo;
    private final ItemRepository itemRepo;

    // ========================================
    // 1. AJUKAN PEMBELIAN (CREATE NEW PESANAN)
    // ========================================
    @Override
    @Transactional
    public Pesanan ajukanPembelian(Long produkId, Long pembeliId) {

        Produk produk = produkRepo.findById(produkId)
                .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));

        if (produk.getStatus() == null || !produk.getStatus().equalsIgnoreCase("Available")) {
            throw new RuntimeException("Produk sudah tidak tersedia");
        }

        Pembeli pembeli = pembeliRepo.findById(pembeliId)
                .orElseThrow(() -> new RuntimeException("Pembeli tidak ditemukan"));

        // Cek Double Request
        List<Pesanan> existing = pesananRepo.findByPembeli_PembeliIdAndProduk_ProdukIdAndStatusPengajuan(
                pembeliId, produkId, StatusPengajuan.PENDING);

        if (!existing.isEmpty()) {
            throw new RuntimeException("Anda sudah mengajukan pembelian produk ini sebelumnya.");
        }

        // Buat Pesanan Baru
        Pesanan pesanan = new Pesanan();
        pesanan.setPembeli(pembeli);
        pesanan.setPenjual(produk.getPenjual());
        pesanan.setStatusPengajuan(StatusPengajuan.PENDING);
        pesanan.setStatusPesanan(StatusPesanan.MENUNGGU_KONFIRMASI_PENJUAL); // Status Awal
        pesanan.setTotalHarga(produk.getHarga());

        // HAPUS jika di Entity Pesanan tidak ada field 'produk'
        // pesanan.setProduk(produk); 

        Pesanan savedPesanan = pesananRepo.save(pesanan);

        // Buat Item (Link antara Pesanan & Produk)
        Item item = new Item();
        item.setPesanan(savedPesanan);
        item.setProduk(produk);
        item.setJumlah(1);
        item.setHargaSatuanSaatIni(produk.getHarga());
        item.setSubtotal(produk.getHarga());
        itemRepo.save(item);

        // Notifikasi Penjual
        createNotifikasi(
            produk.getPenjual(), savedPesanan, 
            pembeli.getNamaLengkap() + " mengajukan pembelian", 
            produk.getNamaProduk(), "MENUNGGU", null
        );

        // Notifikasi Pembeli
        createNotifikasi(
            null, savedPesanan, 
            "Pengajuan Pembelian Berhasil", 
            "Menunggu konfirmasi penjual: " + produk.getNamaProduk(), "MENUNGGU", pembeli
        );

        return savedPesanan;
    }

    // ========================================
    // 2. TERIMA PENGAJUAN (PENJUAL)
    // ========================================
    @Override
    @Transactional
    public Pesanan terimaPengajuan(Long pesananId) {

        Pesanan pesanan = pesananRepo.findById(pesananId)
                .orElseThrow(() -> new RuntimeException("Pesanan tidak ditemukan"));

        if (pesanan.getStatusPengajuan() != StatusPengajuan.PENDING) {
            return pesanan;
        }

        // AMBIL PRODUK DARI ITEM (Supaya Aman dari Null)
        Produk produk = getProdukFromPesanan(pesananId);

        // Update Status Pesanan
        pesanan.setStatusPengajuan(StatusPengajuan.DITERIMA);
        pesanan.setStatusPesanan(StatusPesanan.MENUNGGU_PEMBAYARAN);
        pesananRepo.save(pesanan);

        // Update Notifikasi Lama
        Optional<Notifikasi> maybeNotif = notifRepo.findFirstByPesanan_PesananIdOrderByIdDesc(pesananId);
        maybeNotif.ifPresent(n -> {
            n.setStatus("DITERIMA");
            notifRepo.save(n);
        });

        // Kunci Produk
        if (produk != null) {
            produk.setStatus("Locked");
            produkRepo.save(produk);
            
            // Tolak Pengajuan Lain untuk Produk yang Sama (Opsional, jika sistemnya 1 barang = 1 pembeli)
            // pesananRepo.updateStatusPengajuanForOthers(...) 
        }

        // Notifikasi Pembeli Bahwa Diterima
        createNotifikasi(
            null, pesanan, 
            "Pengajuan Anda diterima! ✅", 
            "Segera lakukan pembayaran untuk: " + (produk != null ? produk.getNamaProduk() : "Barang"), 
            "DITERIMA", pesanan.getPembeli()
        );

        return pesanan;
    }

    // ========================================
    // 3. TOLAK PENGAJUAN (PENJUAL)
    // ========================================
    @Override
    @Transactional
    public void tolakPengajuan(Long pesananId) {

        Pesanan pesanan = pesananRepo.findById(pesananId)
                .orElseThrow(() -> new RuntimeException("Pesanan tidak ditemukan"));

        if (pesanan.getStatusPengajuan() != StatusPengajuan.PENDING) {
            return;
        }

        pesanan.setStatusPengajuan(StatusPengajuan.DITOLAK);
        pesanan.setStatusPesanan(StatusPesanan.DIBATALKAN); // Tambahkan status pesanan juga
        pesananRepo.save(pesanan);

        // Ambil Nama Produk (Safe Way)
        Produk produk = getProdukFromPesanan(pesananId);
        String namaProduk = (produk != null) ? produk.getNamaProduk() : "Barang";

        // Notifikasi Pembeli
        createNotifikasi(
            null, pesanan, 
            "Pengajuan Anda ditolak ❌", 
            namaProduk, "DITOLAK", pesanan.getPembeli()
        );
    }

    // ========================================
    // UTILITIES (Helper Methods)
    // ========================================
    
    // Helper untuk mengambil Produk dari Item (Anti Null Pointer)
    private Produk getProdukFromPesanan(Long pesananId) {
        List<Item> items = itemRepo.findByPesanan_PesananId(pesananId);
        if (!items.isEmpty()) {
            return items.get(0).getProduk();
        }
        return null; // Atau throw exception jika wajib ada
    }

    // Helper untuk bikin notifikasi biar kode gak panjang
    private void createNotifikasi(Object penjualObj, Pesanan pesanan, String judul, String subJudul, String status, Pembeli pembeliObj) {
        Notifikasi notif = new Notifikasi();
        notif.setJudul(judul);
        notif.setSubJudul(subJudul);
        notif.setStatus(status);
        notif.setWaktu(LocalDateTime.now());
        notif.setPesanan(pesanan);

        if (penjualObj != null) notif.setPenjual((com.ubeli.entity.Penjual) penjualObj);
        if (pembeliObj != null) notif.setPembeli(pembeliObj);

        notifRepo.save(notif);
    }
}