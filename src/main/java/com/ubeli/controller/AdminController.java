package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 

import com.ubeli.entity.*;
import com.ubeli.repository.*;
import com.ubeli.enums.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin") 
public class AdminController {

    @Autowired private AdminRepository adminRepo; 
    @Autowired private PesananRepository pesananRepo;
    @Autowired private BannerIklanRepository bannerRepo;
    @Autowired private PembeliRepository pembeliRepo;
    @Autowired private PenjualRepository penjualRepo;
    @Autowired private LaporanRepository laporanRepo;
    @Autowired private ProdukRepository produkRepo;
    

    @GetMapping("/login")
    public String formLoginAdmin() {
        return "admin/login"; 
    }

    // PROSES LOGIN ADMIN
    @PostMapping("/proses-login")
    public String prosesLoginAdmin(@RequestParam String username, 
                                   @RequestParam String password, 
                                   HttpSession session,
                                   Model model) {
        
        // Cari Admin di Database
        Admin admin = adminRepo.findByUsername(username).orElse(null);

        // Validasi Password
        if (admin != null && admin.getPasswordHash().equals(password)) {
            // LOGIN SUKSES
            session.setAttribute("user", admin);
            session.setAttribute("role", "ADMIN"); // Kunci rahasia session
            
            return "redirect:/admin/dashboard";
        } else {
            // LOGIN GAGAL
            model.addAttribute("error", "Username atau Password Salah!");
            return "admin/login";
        }
    }

    // LOGOUT ADMIN
    @GetMapping("/logout")
    public String logoutAdmin(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    // DASHBOARD UTAMA 
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        // Hitung Data Verifikasi (Escrow)
        long totalVerif = pesananRepo.countByStatusPesanan(StatusPesanan.VERIFIKASI_ADMIN);

        // Hitung Iklan Baru
        long totalIklan = bannerRepo.countByStatus(StatusIklan.PENDING);

        // Hitung Laporan Masuk
        long totalLaporan = laporanRepo.countByStatus("Pending");

        // Hitung Total User (Pembeli + Penjual)
        long totalUser = pembeliRepo.count() + penjualRepo.count();

        // Kirim ke HTML
        model.addAttribute("cntVerif", totalVerif);
        model.addAttribute("cntIklan", totalIklan);
        model.addAttribute("cntLaporan", totalLaporan);
        model.addAttribute("cntUser", totalUser);
        
        return "admin/dashboard"; 
    }

    // FITUR TINJAU LAPORAN 
    @GetMapping("/tinjau-laporan")
    public String halamanLaporan(Model model, 
                                 @RequestParam(defaultValue = "pending") String tab) {
        
        List<Laporan> dataTampil = laporanRepo.findAll();

        if (tab.equals("proses")) {
            // TAMPILKAN: Yang sedang ditangani admin
            dataTampil.removeIf(l -> !l.getStatus().equalsIgnoreCase("Proses"));
        } 
        else if (tab.equals("arsip")) {
            // TAMPILKAN: Yang sudah selesai atau ditolak
            dataTampil.removeIf(l -> 
                l.getStatus().equalsIgnoreCase("Pending") || 
                l.getStatus().equalsIgnoreCase("Proses")
            );
        } 
        else {
            dataTampil.removeIf(l -> !l.getStatus().equalsIgnoreCase("Pending"));
        }

        model.addAttribute("listLaporan", dataTampil);
        model.addAttribute("activeTab", tab);
        
        return "admin/tinjau-laporan";
    }

    @PostMapping("/tinjau-laporan/proses")
    public String prosesLaporan(@RequestParam Long laporanId, @RequestParam String keputusan) {
        Laporan l = laporanRepo.findById(laporanId).orElse(null);
        
        if (l != null) {
            if (keputusan.equalsIgnoreCase("PROSES")) {
                l.setStatus("Proses");
            } else if (keputusan.equalsIgnoreCase("SELESAI")) {
                l.setStatus("Selesai");
            } else if (keputusan.equalsIgnoreCase("TOLAK")) {
                l.setStatus("Ditolak");
            }
            
            laporanRepo.save(l);
        }

        if (keputusan.equalsIgnoreCase("PROSES")) {
            return "redirect:/admin/tinjau-laporan?tab=proses";
        } else if (keputusan.equalsIgnoreCase("SELESAI") || keputusan.equalsIgnoreCase("TOLAK")) {
            return "redirect:/admin/tinjau-laporan?tab=arsip";
        } else {
            return "redirect:/admin/tinjau-laporan";
        }
    }

    // FITUR KELOLA PENGGUNA 
    
    // MENAMPILKAN HALAMAN 
    @GetMapping("/kelola-users") 
    public String halamanKelolaUser(Model model,
                                    @RequestParam(defaultValue = "") String keyword,
                                    @RequestParam(defaultValue = "0") int pagePembeli, 
                                    @RequestParam(defaultValue = "0") int pagePenjual) { 
        
        int pageSize = 5; 

        Page<Pembeli> listPembeli;
        Page<Penjual> listPenjual;

        if (keyword.isEmpty()) {
            listPembeli = pembeliRepo.findAll(PageRequest.of(pagePembeli, pageSize));
            listPenjual = penjualRepo.findAll(PageRequest.of(pagePenjual, pageSize));
        } else {
            listPembeli = pembeliRepo.findByNamaLengkapContainingIgnoreCase(keyword, PageRequest.of(pagePembeli, pageSize));
            listPenjual = penjualRepo.findByNamaLengkapContainingIgnoreCase(keyword, PageRequest.of(pagePenjual, pageSize));
        }

        // Kirim Data ke HTML
        model.addAttribute("listPembeli", listPembeli);
        model.addAttribute("listPenjual", listPenjual);
        model.addAttribute("keyword", keyword); 
        model.addAttribute("currentPagePembeli", pagePembeli);
        model.addAttribute("currentPagePenjual", pagePenjual);
        
        return "admin/kelola-users"; 
    }

    // PROSES BLOKIR 
    @PostMapping("/kelola-users/blokir")
    public String blokirUser(@RequestParam Long userId, 
                             @RequestParam String role, 
                             @RequestParam String status,
                             RedirectAttributes redirectAttributes) {
        
        String namaUser = ""; 

        if (role.equalsIgnoreCase("PEMBELI")) {
            Pembeli p = pembeliRepo.findById(userId).orElse(null);
            if (p != null) {
                p.setStatus(status);
                pembeliRepo.save(p);
                namaUser = p.getNamaLengkap();
            }
        } else if (role.equalsIgnoreCase("PENJUAL")) {
            Penjual p = penjualRepo.findById(userId).orElse(null);
            if (p != null) {
                p.setStatus(status);
                penjualRepo.save(p);
                namaUser = p.getNamaLengkap();
            }
        }

        // Notif
        if (status.equals("BANNED")) {
            redirectAttributes.addFlashAttribute("msgType", "danger");
            redirectAttributes.addFlashAttribute("msgTitle", "Akun Diblokir!");
            redirectAttributes.addFlashAttribute("msgBody", "Pengguna <b>" + namaUser + "</b> telah dinonaktifkan.");
        } else {
            redirectAttributes.addFlashAttribute("msgType", "success");
            redirectAttributes.addFlashAttribute("msgTitle", "Blokir Dibuka");
            redirectAttributes.addFlashAttribute("msgBody", "Akses akun <b>" + namaUser + "</b> telah dipulihkan.");
        }

        return "redirect:/admin/kelola-users";
    }

    // FITUR VERIFIKASI PEMBAYARAN 
    @GetMapping("/verifikasi-bayar")
    public String halamanVerifikasi(Model model, 
                                    @RequestParam(defaultValue = "pending") String tab) {
        
        List<Pesanan> dataTampil;

        if (tab.equals("dibatalkan")) {
            dataTampil = pesananRepo.findAll();
            dataTampil.removeIf(p -> 
                !(p.getStatusPesanan() == StatusPesanan.DIBATALKAN)
            );
        } 
        else if (tab.equals("riwayat")) {
            dataTampil = pesananRepo.findAll();
            dataTampil.removeIf(p -> 
                !(p.getStatusPesanan() == StatusPesanan.DIBAYAR || 
                  p.getStatusPesanan() == StatusPesanan.DIKIRIM || 
                  p.getStatusPesanan() == StatusPesanan.SELESAI)
            );
        } 
        else {
            dataTampil = pesananRepo.findByStatusPesanan(StatusPesanan.VERIFIKASI_ADMIN);
        }

        model.addAttribute("listPesanan", dataTampil);
        model.addAttribute("activeTab", tab);
        
        return "admin/verifikasi-pembayaran";
    }

    @PostMapping("/verifikasi-bayar/proses")
    public String prosesVerifikasi(@RequestParam Long pesananId, @RequestParam String aksi) {
        Pesanan p = pesananRepo.findById(pesananId).orElse(null);
        if (p != null) {
            if (aksi.equals("TERIMA")) {
                p.setStatusPesanan(StatusPesanan.DIBAYAR); 
            } else {
                p.setStatusPesanan(StatusPesanan.DIBATALKAN); 
            }
            pesananRepo.save(p);
        }
        return "redirect:/admin/verifikasi-bayar?tab=pending";
    }

    @GetMapping("/validasi-iklan")
    public String halamanValidasiIklan(Model model, 
                                       @RequestParam(defaultValue = "pending") String tab) {
        
        List<BannerIklan> semuaIklan = bannerRepo.findAll();
        LocalDate hariIni = LocalDate.now();

        // LOGIKA FILTERING
        if (tab.equals("active")) {
            semuaIklan.removeIf(i -> 
                !(i.getStatus() == StatusIklan.ACTIVE && 
                 (i.getTanggalSelesai().isAfter(hariIni) || i.getTanggalSelesai().isEqual(hariIni)))
            );

        } else if (tab.equals("riwayat")) {
            semuaIklan.removeIf(i -> 
                !(i.getStatus() == StatusIklan.REJECTED || 
                  i.getStatus() == StatusIklan.EXPIRED ||
                 (i.getStatus() == StatusIklan.ACTIVE && i.getTanggalSelesai().isBefore(hariIni)))
            );

        } else {
            semuaIklan.removeIf(i -> i.getStatus() != StatusIklan.PENDING);
        }
        
        model.addAttribute("listIklan", semuaIklan);
        model.addAttribute("activeTab", tab); 
        
        return "admin/validasi-iklan";
    }

    @PostMapping("/validasi-iklan/proses")
    public String prosesIklan(@RequestParam Long iklanId, @RequestParam String aksi) {
        BannerIklan iklan = bannerRepo.findById(iklanId).orElse(null);
        
        if (iklan != null) {
            if (aksi.equals("TERIMA")) {
                iklan.setStatus(StatusIklan.ACTIVE);
                iklan.setTanggalMulai(LocalDate.now());
                
                int durasi = iklan.getJenisPaket().getDurasiHari();
                iklan.setTanggalSelesai(LocalDate.now().plusDays(durasi));
                
                Produk p = iklan.getProduk();
                p.setDiiklankan(true);
                p.setPeriodeIklan(iklan.getTanggalSelesai());
                
                produkRepo.save(p);
            } else {
                iklan.setStatus(StatusIklan.REJECTED);
            }
            bannerRepo.save(iklan);
        }
        return "redirect:/admin/validasi-iklan";
    }
}