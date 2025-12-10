package com.ubeli.repository;

import com.ubeli.entity.Laporan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LaporanRepository extends JpaRepository<Laporan, Long> {

    // Cari semua laporan dengan status tertentu (misal: "Pending")
    List<Laporan> findByStatus(String status);

    // Hitung laporan yang statusnya "Pending"
    long countByStatus(String status);
}