package com.ubeli.repository;

import com.ubeli.entity.Pembeli;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PembeliRepository extends JpaRepository<Pembeli, Long> {

    // Penting buat Login: Cari user berdasarkan email
    Optional<Pembeli> findByEmail(String email);
    
    // Cek apakah email sudah terdaftar (saat register)
    boolean existsByEmail(String email);
    //Pembeli findByEmailAndPassword(String email, String password);
}
