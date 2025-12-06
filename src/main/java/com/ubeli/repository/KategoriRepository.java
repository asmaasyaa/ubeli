package com.ubeli.repository;

import com.ubeli.entity.Kategori;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KategoriRepository extends JpaRepository<Kategori, Long> {
}