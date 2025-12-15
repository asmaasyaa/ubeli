package com.ubeli.controller.penjual;

import com.ubeli.entity.Penjual;
import com.ubeli.entity.Produk;
import com.ubeli.repository.*;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PenjualProfilController {

    @Autowired
    private ProdukRepository produkRepo;

    @Autowired
    private PenjualRepository penjualRepo;

    // ==========================================
    // 1. HALAMAN PROFIL (READ)
    // ==========================================
    @GetMapping("/penjual/profil")
    public String profilPenjual(HttpSession session, Model model) {

        // 1. Ambil Penjual dari Session (Cuma buat ambil ID)
        Penjual sessionPenjual = (Penjual) session.getAttribute("penjual");

        // 2. Cek Login & Role
        if (sessionPenjual == null || !"PENJUAL".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        // 3. AMBIL DATA TERBARU DARI DATABASE (PENTING! Biar data gak hilang/sinkron)
        // Kita gunakan ID dari session untuk mencari data asli di DB
        Penjual penjualDb = penjualRepo.findById(sessionPenjual.getPenjualId()).orElse(null);
        
        if (penjualDb == null) {
            session.invalidate();
            return "redirect:/login";
        }

        // 4. Update Session dengan data terbaru
        session.setAttribute("penjual", penjualDb);

        // Ambil Produk
        List<Produk> produkList = produkRepo.findByPenjual_PenjualId(penjualDb.getPenjualId());

        model.addAttribute("penjual", penjualDb); // Kirim data DB ke HTML
        model.addAttribute("produkList", produkList);

        return "penjual/profil";
    }
    
    // ==========================================
    // 2. HALAMAN EDIT PROFIL (FORM)
    // ==========================================
    @GetMapping("/penjual/edit-profil")
    public String editProfil(HttpSession session, Model model) {

        Penjual sessionPenjual = (Penjual) session.getAttribute("penjual");

        if (sessionPenjual == null || !"PENJUAL".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        // AMBIL DATA SEGAR DARI DATABASE
        Penjual penjualDb = penjualRepo.findById(sessionPenjual.getPenjualId()).orElse(null);

        model.addAttribute("penjual", penjualDb); // Pastikan HTML pakai data DB

        return "penjual/edit-profil"; 
    }

    // ==========================================
    // 3. PROSES UPDATE PROFIL (ACTION)
    // ==========================================
    @PostMapping("/penjual/update-profil")
    public String updateProfil(
            @RequestParam String namaLengkap,
            @RequestParam String email,
            @RequestParam String noHp,
            @RequestParam(required = false) String deskripsi,
            @RequestParam(required = false) String lokasiToko,
            
            // === TAMBAHAN BARU (WAJIB ADA) ===
            @RequestParam(required = false) String namaBank,
            @RequestParam(required = false) String noRekening,
            
            // Tangkap checkbox payment
            @RequestParam(value = "metodePembayaran", required = false) List<String> metodeList, 
            HttpSession session
    ) {

        Penjual sessionPenjual = (Penjual) session.getAttribute("penjual");

        if (sessionPenjual == null || !"PENJUAL".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        // 1. Ambil data lama dari DB berdasarkan ID di session
        Penjual penjual = penjualRepo.findById(sessionPenjual.getPenjualId()).orElse(null);
        if (penjual == null) return "redirect:/login";

        // 2. Update Field Dasar
        penjual.setNamaLengkap(namaLengkap);
        penjual.setEmail(email);
        penjual.setNoHp(noHp);
        penjual.setDeskripsiToko(deskripsi); 
        penjual.setLokasiToko(lokasiToko);
        
        // 3. Update Bank & Rekening
        penjual.setNamaBank(namaBank);
        penjual.setNoRekening(noRekening);

        // 4. Update Metode Pembayaran
        if (metodeList != null && !metodeList.isEmpty()) {
            String metodeGabungan = String.join(",", metodeList);
            penjual.setMetodePembayaran(metodeGabungan);
        } else {
            penjual.setMetodePembayaran(null);
        }

        // 5. SIMPAN KE DB & UPDATE SESSION
        Penjual savedPenjual = penjualRepo.save(penjual); 
        session.setAttribute("penjual", savedPenjual); // Simpan hasil save ke session

        return "redirect:/penjual/profil?success=true"; 
    }
}