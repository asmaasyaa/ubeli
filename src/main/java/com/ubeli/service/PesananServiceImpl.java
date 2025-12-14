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
import java.math.BigDecimal;
import java.util.Optional; // Digunakan untuk findById
import java.util.List;
import java.util.Optional;

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

        // Pastikan produk tersedia
        if (produk.getStatus() == null || !produk.getStatus().equalsIgnoreCase("Available")) {
            throw new RuntimeException("Produk sudah tidak tersedia");
        }

        Pembeli pembeli = pembeliRepo.findById(pembeliId)
                .orElseThrow(() -> new RuntimeException("Pembeli tidak ditemukan"));

        // Cek apakah pembeli sudah mengajukan 
        List<Pesanan> existing = pesananRepo.findByPembeli_PembeliIdAndProduk_ProdukIdAndStatusPengajuan(
                pembeliId, produkId, StatusPengajuan.PENDING);

        if (!existing.isEmpty()) {
            throw new RuntimeException("Anda sudah mengajukan pembelian produk ini sebelumnya.");
        }

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

    // 2. PENJUAL MENERIMA SALAH SATU PENGAJUAN
    @Override
    @Transactional
    public Pesanan terimaPengajuan(Long pesananId) {

        Pesanan pesanan = pesananRepo.findById(pesananId)
                .orElseThrow(() -> new RuntimeException("Pesanan tidak ditemukan"));

        if (pesanan.getStatusPengajuan() != StatusPengajuan.PENDING) {
            return pesanan;
        }

        Produk produk = pesanan.getProduk();

        // Terima pesanan 
        pesanan.setStatusPengajuan(StatusPengajuan.DITERIMA);
        // *SET STATUS PESANAN AWAL AGAR PEMBELI TAHU HARUS BAYAR*
        pesanan.setStatusPesanan(StatusPesanan.MENUNGGU_PEMBAYARAN); 
        pesananRepo.save(pesanan);

        Optional<Notifikasi> maybeNotif = notifRepo.findFirstByPesanan_PesananIdOrderByIdDesc(pesananId);
        maybeNotif.ifPresent(n -> {
            n.setStatus("DITERIMA");
            notifRepo.save(n);
        });

        pesananRepo.updateStatusPengajuanForOthers(
                produk.getProdukId(),
                pesananId,
                StatusPengajuan.DITOLAK
        );

        // Update notifikasi pembeli lain menjadi DITOLAK 
        List<Notifikasi> notifsOthers = notifRepo.findByPesanan_Produk_ProdukIdAndPesanan_PesananIdNot(
                produk.getProdukId(), pesananId);
        for (Notifikasi n : notifsOthers) {
            n.setStatus("DITOLAK");
            notifRepo.save(n);
        }

        produk.setStatus("Locked");
        produkRepo.save(produk);

        // Kirim notifikasi ke pembeli yang DITERIMA 
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

    // 3. PENJUAL MENOLAK PENGAJUAN
    @Override
    @Transactional
    public void tolakPengajuan(Long pesananId) {

        Pesanan pesanan = pesananRepo.findById(pesananId)
                .orElseThrow(() -> new RuntimeException("Pesanan tidak ditemukan"));

        // jika sudah diterima/ditolak, skip
        if (pesanan.getStatusPengajuan() != null && pesanan.getStatusPengajuan() != StatusPengajuan.PENDING) {
            return;
        }

        pesanan.setStatusPengajuan(StatusPengajuan.DITOLAK);
        pesananRepo.save(pesanan);
        
        // Produk dikembalikan statusnya (tidak dibahas di sini)

        // Buat notifikasi ke pembeli
        Notifikasi notif = new Notifikasi();
        notif.setJudul("Pengajuan Anda ditolak");
        notif.setSubJudul(pesanan.getProduk().getNamaProduk());
        notif.setWaktu(LocalDateTime.now());
        notif.setStatus("DITOLAK");
        notif.setPembeli(pesanan.getPembeli());

        notifRepo.save(notif);
    }
}