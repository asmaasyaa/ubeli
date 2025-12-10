package com.ubeli.repository;

import com.ubeli.entity.Pesanan;
import com.ubeli.enums.StatusPengajuan;
import com.ubeli.enums.StatusPesanan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PesananRepository extends JpaRepository<Pesanan, Long> {

    // Cari riwayat belanjaan Pembeli A
    List<Pesanan> findByPembeli_PembeliId(Long pembeliId);

    // Cari pesanan masuk untuk Penjual B (Buat Dashboard Toko)
    List<Pesanan> findByPenjual_PenjualId(Long penjualId);

    List<Pesanan> findByStatusPesanan(StatusPesanan status);

    // Hitung pesanan yang statusnya VERIFIKASI_ADMIN
    long countByStatusPesanan(StatusPesanan status);

    List<Pesanan> findByProduk_ProdukId(Long produkId);

    // Ambil hanya PENDING pengajuan untuk 1 produk
    List<Pesanan> findByProduk_ProdukIdAndStatusPengajuan(Long produkId, StatusPengajuan status);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Pesanan p SET p.statusPengajuan = :status WHERE p.produk.produkId = :produkId AND p.pesananId <> :pesananDiterima")
    void updateStatusPengajuanForOthers(Long produkId, Long pesananDiterima, StatusPengajuan status);
}