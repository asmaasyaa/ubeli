package com.ubeli.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ubeli.repository.PembeliRepository;
import com.ubeli.entity.Pembeli;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private PembeliRepository pembeliRepository;

    public boolean login(String email, String password) {

        // Ambil data pembeli berdasarkan email
        Optional<Pembeli> pembeliOpt = pembeliRepository.findByEmail(email);

        // Kalau email tidak ditemukan
        if (pembeliOpt.isEmpty()) {
            return false;
        }

        Pembeli pembeli = pembeliOpt.get();

        // --- CASE 1: Kamu belum pakai hashing (sementara plaintext) ---
        // Cocokkan langsung:
        return pembeli.getPasswordHash().equals(password);

        // --- CASE 2: Kalau pakai BCrypt (nanti) ---
        // return passwordEncoder.matches(password, pembeli.getPasswordHash());
    }
}
