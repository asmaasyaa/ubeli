package com.ubeli.controller;

import org.springframework.stereotype.Controller;
// import com.ubeli.repository.PembeliRepository;
// import com.ubeli.repository.PenjualRepository;

@Controller
public class AuthController {

    // Method Login
    public String validasiLogin(String email, String password) {
        // Logic cek email & password di database
        // Cek tabel Pembeli dulu, kalau gak ada cek tabel Penjual
        System.out.println("Mengecek login user: " + email);
        return "redirect:/home";
    }

    // Method Register Pembeli
    public String registrasiUser(String nama, String email, String pass) {
        // Simpan ke tabel Pembeli
        System.out.println("Mendaftarkan user baru: " + nama);
        return "redirect:/login";
    }
}