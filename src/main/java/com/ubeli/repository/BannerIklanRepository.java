package com.ubeli.repository;

import com.ubeli.entity.BannerIklan;
import com.ubeli.enums.StatusIklan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BannerIklanRepository extends JpaRepository<BannerIklan, Long> {

    @Query("SELECT b FROM BannerIklan b WHERE b.status = :status AND b.tanggalSelesai >= :hariIni")
    List<BannerIklan> findActiveAds(@Param("status") StatusIklan status, @Param("hariIni") LocalDate hariIni);
    
    // Query buat admin 
    List<BannerIklan> findByStatus(StatusIklan status);
    long countByStatus(StatusIklan status);
}