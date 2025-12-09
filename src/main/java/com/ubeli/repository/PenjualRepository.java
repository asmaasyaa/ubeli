package com.ubeli.repository;

import com.ubeli.entity.Penjual;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PenjualRepository extends JpaRepository<Penjual, Long> {

    // Cari penjual by email
    Optional<Penjual> findByEmail(String email);
    //Penjual findByEmailAndPassword(String email, String passwordHash);

    // Sama seperti pembeli
    Page<Penjual> findByNamaLengkapContainingIgnoreCase(String keyword, Pageable pageable);
}