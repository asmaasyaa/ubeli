package com.ubeli.repository;

import com.ubeli.entity.Penjual;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PenjualRepository extends JpaRepository<Penjual, Long> {

    // Cari penjual by email
    Optional<Penjual> findByEmail(String email);
}