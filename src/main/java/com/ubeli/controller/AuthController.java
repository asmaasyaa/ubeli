package com.ubeli.controller;

import com.ubeli.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class AuthController {

    private final AuthService authService;

    // Constructor injection
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "general/login";
    }

    @PostMapping("/login")
    public String loginProcess(
            @RequestParam String email,
            @RequestParam String password,
            Model model
    ) {
        boolean success = authService.login(email, password);

        if(success) {
            return "redirect:/home";
        }

        model.addAttribute("error", "Email atau password salah");
        return "general/login";
    }
}
