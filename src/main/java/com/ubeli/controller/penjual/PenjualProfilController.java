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
// import org.springframework.web.multipart.MultipartFile; // Aktifkan jika sudah ada logic upload foto

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

        // CEK ROLE
        if (!"PENJUAL".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        // CEK OBJEK PENJUAL DI SESSION
        Penjual penjual = (Penjual) session.getAttribute("penjual");

        if (penjual == null) {  
            session.invalidate();
            return "redirect:/login";
        }

        // AMBIL PRODUK
        List<Produk> produkList = produkRepo.findByPenjual_PenjualId(penjual.getPenjualId());

        model.addAttribute("penjual", penjual);
        model.addAttribute("produkList", produkList);

        return "penjual/profil";
    }
    
    // ==========================================
    // 2. HALAMAN EDIT PROFIL (FORM)
    // ==========================================
    @GetMapping("/penjual/edit-profil")
    public String editProfil(HttpSession session, Model model) {

        // CEK ROLE
        if (!"PENJUAL".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        Penjual penjual = (Penjual) session.getAttribute("penjual");

        if (penjual == null) {
            session.invalidate();
            return "redirect:/login";
        }

        model.addAttribute("penjual", penjual);

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
            
            // --- PERBAIKAN UTAMA ADA DI SINI ---
            // Kita tangkap checkbox dari HTML (name="metodePembayaran") 
            // lalu masukkan ke variable Java bernama 'metodeList'
            @RequestParam(value = "metodePembayaran", required = false) List<String> metodeList, 
            
            // @RequestParam(value = "foto", required = false) MultipartFile foto, // (Opsional: Aktifkan jika mau handle upload foto)

            HttpSession session,
            Model model
    ) {

        // 1. CEK SESSION
        if (!"PENJUAL".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        Penjual penjual = (Penjual) session.getAttribute("penjual");

        if (penjual == null) {
            session.invalidate();
            return "redirect:/login";
        }

        // 2. UPDATE DATA DASAR
        penjual.setNamaLengkap(namaLengkap);
        penjual.setEmail(email);
        penjual.setNoHp(noHp);
        penjual.setDeskripsiToko(deskripsi); 
        penjual.setLokasiToko(lokasiToko);

        // 3. LOGIKA METODE PEMBAYARAN (GABUNGKAN LIST JADI STRING)
        // Checkbox HTML mengirim list ["COD", "QRIS"], kita ubah jadi "COD,QRIS" buat database
        if (metodeList != null && !metodeList.isEmpty()) {
            String metodeGabungan = String.join(",", metodeList);
            penjual.setMetodePembayaran(metodeGabungan);
        } else {
            // Jika user uncheck semua, set null di database
            penjual.setMetodePembayaran(null);
        }

        // 4. SIMPAN KE DATABASE
        penjualRepo.save(penjual); 

        // 5. UPDATE SESSION (Penting! Biar pas redirect datanya langsung berubah)
        session.setAttribute("penjual", penjual);

        return "redirect:/penjual/profil?success=true";
    }
}