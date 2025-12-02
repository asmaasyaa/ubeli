package com.ubeli.repository;

import com.ubeli.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    // Admin biasanya login pakai username
    Optional<Admin> findByUsername(String username);
}