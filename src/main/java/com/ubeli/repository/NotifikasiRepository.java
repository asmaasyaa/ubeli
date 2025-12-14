package com.ubeli.repository;

import com.ubeli.entity.Notifikasi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotifikasiRepository extends JpaRepository<Notifikasi, Long> {

    // Notifikasi untuk pembeli
    List<Notifikasi> findByPembeli_PembeliId(Long pembeliId);

    // Notifikasi untuk penjual
    List<Notifikasi> findByPenjual_PenjualId(Long penjualId);

    // Ambil notifikasi terkait pesanan (jika ada) - ambil yang terbaru
    Optional<Notifikasi> findFirstByPesanan_PesananIdOrderByIdDesc(Long pesananId);

    // Cari notifikasi untuk produk tertentu tapi bukan pesanan tertentu
    List<Notifikasi> findByPesanan_Produk_ProdukIdAndPesanan_PesananIdNot(Long produkId, Long pesananId);

    // Jika butuh cari by pesanan id (semua)
    List<Notifikasi> findByPesanan_PesananId(Long pesananId);
}
