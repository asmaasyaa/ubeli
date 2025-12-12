package com.ubeli.controller;

import com.ubeli.entity.Pembeli;
import com.ubeli.entity.Penjual;
import com.ubeli.repository.PembeliRepository;
import com.ubeli.repository.PenjualRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private PembeliRepository pembeliRepo;

    @Autowired
    private PenjualRepository penjualRepo;


    // LOGIN PAGE
    @GetMapping("/login")
    public String loginPage() {
        return "general/login";
    }

    // LOGIN PROCESS
    @PostMapping("/login")
    public String doLogin(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            HttpSession session
    ) {

        // LOGIN PEMBELI 
        if (role.equals("PEMBELI")) {

            Pembeli pembeli = pembeliRepo.findByEmail(email).orElse(null);

            if (pembeli == null || !pembeli.getPasswordHash().equals(password)) {
                return "redirect:/login?error";
            }

            session.setAttribute("pembeli", pembeli);
            session.setAttribute("userId", pembeli.getPembeliId());
            session.setAttribute("pembeliId", pembeli.getPembeliId());
            session.setAttribute("role", "PEMBELI");
            session.removeAttribute("penjual");
        }

        // LOGIN PENJUAL 
        else if (role.equals("PENJUAL")) {

            Penjual penjual = penjualRepo.findByEmail(email).orElse(null);

            if (penjual == null || !penjual.getPasswordHash().equals(password)) {
                return "redirect:/login?error";
            }

            session.setAttribute("penjual", penjual);
            session.setAttribute("penjualId", penjual.getPenjualId());
            session.setAttribute("userId", penjual.getPenjualId());
            session.setAttribute("role", "PENJUAL");
            session.removeAttribute("pembeli");
        }

        if (role.equals("PEMBELI")) {
            return "redirect:/";
        } else {
            return "redirect:/penjual/profil";
        }
    }


    
    // REGISTER PAGE
    @GetMapping("/register")
    public String registerPage() {
        return "general/registration";
    }

    // REGISTER PROCESS
    @PostMapping("/register")
    public String doRegister(
            @RequestParam String namaLengkap,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String noHp,
            @RequestParam String noRekening,
            @RequestParam String bank,
            @RequestParam String lokasiToko,
            @RequestParam(required = false) String deskripsiToko
            
    ) {

        if (pembeliRepo.existsByEmail(email)) {
            return "redirect:/register?error=email";
        }

        // BUAT PEMBELI 
        Pembeli pembeli = new Pembeli();
        pembeli.setNamaLengkap(namaLengkap);
        pembeli.setEmail(email);
        pembeli.setPasswordHash(password);
        pembeli.setNoHp(noHp);
        pembeli.setStatus("Active");

        pembeliRepo.save(pembeli);


        // BUAT PENJUAL 
        Penjual penjual = new Penjual();
        penjual.setNamaLengkap(namaLengkap);
        penjual.setEmail(email);
        penjual.setPasswordHash(password);
        penjual.setNoHp(noHp);
        penjual.setLokasiToko(lokasiToko);
        penjual.setBank(bank);
        penjual.setNoRekening(noRekening);
        penjual.setDeskripsiToko(deskripsiToko);
        penjual.setStatus("Active");

        penjualRepo.save(penjual);

        return "redirect:/login?registered";
    }

    // LOGOUT
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
