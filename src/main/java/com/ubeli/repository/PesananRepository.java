package com.ubeli.repository;

import com.ubeli.entity.Pesanan;
import com.ubeli.enums.StatusPesanan;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PesananRepository extends JpaRepository<Pesanan, Long> {

    // Cari riwayat belanjaan Pembeli A
    List<Pesanan> findByPembeli_PembeliId(Long pembeliId);

    // Cari pesanan masuk untuk Penjual B (Buat Dashboard Toko)
    List<Pesanan> findByPenjual_PenjualId(Long penjualId);

    List<Pesanan> findByStatusPesanan(StatusPesanan status);

    // Hitung pesanan yang statusnya VERIFIKASI_ADMIN
    long countByStatusPesanan(StatusPesanan status);
}