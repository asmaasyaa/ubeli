package com.ubeli.repository;

import com.ubeli.entity.BannerIklan;
import com.ubeli.enums.StatusIklan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BannerIklanRepository extends JpaRepository<BannerIklan, Long> {

    // Cari iklan yang statusnya masih PENDING (Buat Admin ACC)
    List<BannerIklan> findByStatus(StatusIklan status);
}