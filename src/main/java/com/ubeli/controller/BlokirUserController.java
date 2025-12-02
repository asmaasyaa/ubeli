package com.ubeli.controller;

import org.springframework.stereotype.Controller;
// import com.ubeli.repository.PembeliRepository;
// import com.ubeli.repository.PenjualRepository;

@Controller
public class BlokirUserController {

    // Method Blokir
    public void setStatusUser(Long userId, String role, String status) {
        // Logic:
        // 1. Cek role (Pembeli atau Penjual)
        // 2. Cari user by ID
        // 3. Update status jadi "BANNED"
        
        System.out.println("Admin mengubah status user ID " + userId + " menjadi: " + status);
    }
}