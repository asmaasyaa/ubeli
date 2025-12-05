package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "general/login";
    }

    @PostMapping("/login")
    public String loginProcess(
            @RequestParam String email,
            @RequestParam String password
    ) {
        // nanti dicek database
        System.out.println("Login: " + email + " - " + password);

        return "redirect:/home";
    }

    // Method Login (sementara tidak dipakai)
    public String validasiLogin(String email, String password) {
        System.out.println("Mengecek login user: " + email);
        return "redirect:/home";
    }

    public String registrasiUser(String nama, String email, String pass) {
        System.out.println("Mendaftarkan user baru: " + nama);
        return "redirect:/login";
    }
}
