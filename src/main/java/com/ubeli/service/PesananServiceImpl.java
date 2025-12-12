package com.ubeli.service;

import com.ubeli.entity.Pesanan;
import com.ubeli.entity.Produk;
import com.ubeli.entity.Pembeli;
import com.ubeli.entity.Notifikasi;
import com.ubeli.enums.StatusPengajuan;
import com.ubeli.repository.PesananRepository;
import com.ubeli.repository.ProdukRepository;
import com.ubeli.repository.NotifikasiRepository;
import com.ubeli.repository.PembeliRepository;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PesananServiceImpl implements com.ubeli.service.PesananService {

    private final PesananRepository pesananRepo;
    private final ProdukRepository produkRepo;
    private final PembeliRepository pembeliRepo;
    private final NotifikasiRepository notifRepo;

    // 1. AJUKAN PEMBELIAN
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
        pesanan.setProduk(produk);
        pesanan.setPembeli(pembeli);
        pesanan.setPenjual(produk.getPenjual());
        pesanan.setStatusPengajuan(StatusPengajuan.PENDING);
        pesanan.setStatusPesanan(null);
        pesanan.setTotalHarga(produk.getHarga());

        Pesanan saved = pesananRepo.save(pesanan);

        // NOTIFIKASI UNTUK PENJUAL 
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
        notif.setSubJudul(pesanan.getProduk().getNamaProduk());
        notif.setStatus("DITERIMA");
        notif.setWaktu(LocalDateTime.now().toString());
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

        // Buat notifikasi ke pembeli
        Notifikasi notif = new Notifikasi();
        notif.setJudul("Pengajuan Anda ditolak");
        notif.setSubJudul(pesanan.getProduk().getNamaProduk());
        notif.setWaktu(LocalDateTime.now().toString());
        notif.setStatus("DITOLAK");
        notif.setPembeli(pesanan.getPembeli());
        notif.setPesanan(pesanan);
        notifRepo.save(notif);
    }
}
