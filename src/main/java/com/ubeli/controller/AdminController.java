package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Jangan lupa import ini

import com.ubeli.entity.*;
import com.ubeli.repository.*;
import com.ubeli.enums.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin") // Semua URL diawali /admin
public class AdminController {

    // Panggil semua repo yang dibutuhkan Admin
    @Autowired private AdminRepository adminRepo; // Pastikan ini ada di paling atas
    @Autowired private PesananRepository pesananRepo;
    @Autowired private BannerIklanRepository bannerRepo;
    @Autowired private PembeliRepository pembeliRepo;
    @Autowired private PenjualRepository penjualRepo;
    @Autowired private LaporanRepository laporanRepo;
    @Autowired private ProdukRepository produkRepo;
    

    // 1. TAMPILKAN HALAMAN LOGIN ADMIN
    @GetMapping("/login")
    public String formLoginAdmin() {
        return "admin/login"; // Mengarah ke templates/admin/login.html
    }

    // 2. PROSES LOGIN ADMIN
    @PostMapping("/proses-login")
    public String prosesLoginAdmin(@RequestParam String username, 
                                   @RequestParam String password, 
                                   HttpSession session,
                                   Model model) {
        
        // Cari Admin di Database
        Admin admin = adminRepo.findByUsername(username).orElse(null);

        // Validasi Password (Sederhana)
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

    // 3. LOGOUT ADMIN
    @GetMapping("/logout")
    public String logoutAdmin(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    // --- 0. DASHBOARD UTAMA (DINAMIS) ---
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // (Optional) Cek Login Admin
        // if (session.getAttribute("user") == null) return "redirect:/admin/login";

        // 1. Hitung Data Verifikasi (Escrow)
        long totalVerif = pesananRepo.countByStatusPesanan(StatusPesanan.VERIFIKASI_ADMIN);

        // 2. Hitung Iklan Baru
        long totalIklan = bannerRepo.countByStatus(StatusIklan.PENDING);

        // 3. Hitung Laporan Masuk
        long totalLaporan = laporanRepo.countByStatus("Pending");

        // 4. Hitung Total User (Pembeli + Penjual)
        long totalUser = pembeliRepo.count() + penjualRepo.count();

        // 5. Kirim ke HTML
        model.addAttribute("cntVerif", totalVerif);
        model.addAttribute("cntIklan", totalIklan);
        model.addAttribute("cntLaporan", totalLaporan);
        model.addAttribute("cntUser", totalUser);
        
        return "admin/dashboard"; 
    }

    // --- 1. FITUR TINJAU LAPORAN (Gabungan TinjauLaporanController) ---
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
            // DEFAULT (PENDING): Laporan baru masuk
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
            // Keputusan bisa: "Proses", "Selesai", "Ditolak"
            // Case insensitive biar aman
            if (keputusan.equalsIgnoreCase("PROSES")) {
                l.setStatus("Proses");
            } else if (keputusan.equalsIgnoreCase("SELESAI")) {
                l.setStatus("Selesai");
            } else if (keputusan.equalsIgnoreCase("TOLAK")) {
                l.setStatus("Ditolak");
            }
            
            laporanRepo.save(l);
        }
        // Redirect balik ke tab yang sesuai biar UX enak
        if (keputusan.equalsIgnoreCase("PROSES")) {
            return "redirect:/admin/tinjau-laporan?tab=proses";
        } else if (keputusan.equalsIgnoreCase("SELESAI") || keputusan.equalsIgnoreCase("TOLAK")) {
            return "redirect:/admin/tinjau-laporan?tab=arsip";
        } else {
            return "redirect:/admin/tinjau-laporan";
        }
    }

    // --- 2. FITUR KELOLA PENGGUNA ---
    
    // MENAMPILKAN HALAMAN (URL: /admin/kelola-users)
    @GetMapping("/kelola-users") // Pastikan mappingnya sesuai yg kamu pakai
    public String halamanKelolaUser(Model model,
                                    @RequestParam(defaultValue = "") String keyword,
                                    @RequestParam(defaultValue = "0") int pagePembeli, // Default halaman 0 (pertama)
                                    @RequestParam(defaultValue = "0") int pagePenjual) { // Default halaman 0
        
        int pageSize = 5; // Mau nampilin berapa baris per halaman?

        Page<Pembeli> listPembeli;
        Page<Penjual> listPenjual;

        if (keyword.isEmpty()) {
            // Kalau gak nyari apa-apa, tampilkan semua + pagination
            listPembeli = pembeliRepo.findAll(PageRequest.of(pagePembeli, pageSize));
            listPenjual = penjualRepo.findAll(PageRequest.of(pagePenjual, pageSize));
        } else {
            // Kalau ada keyword, cari berdasarkan nama
            listPembeli = pembeliRepo.findByNamaLengkapContainingIgnoreCase(keyword, PageRequest.of(pagePembeli, pageSize));
            listPenjual = penjualRepo.findByNamaLengkapContainingIgnoreCase(keyword, PageRequest.of(pagePenjual, pageSize));
        }

        // Kirim Data ke HTML
        model.addAttribute("listPembeli", listPembeli);
        model.addAttribute("listPenjual", listPenjual);
        model.addAttribute("keyword", keyword); // Balikin keyword biar gak ilang pas pindah halaman
        model.addAttribute("currentPagePembeli", pagePembeli);
        model.addAttribute("currentPagePenjual", pagePenjual);
        
        return "admin/kelola-users"; // Sesuaikan nama file HTML kamu
    }

    // PROSES BLOKIR (Action: /admin/kelola-users/blokir)
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

        // Redirect balik ke URL yang ada 's' nya
        return "redirect:/admin/kelola-users";
    }

    // --- 3. FITUR VERIFIKASI PEMBAYARAN (3 TAB: Pending, Dibatalkan, Sukses) ---
    @GetMapping("/verifikasi-bayar")
    public String halamanVerifikasi(Model model, 
                                    @RequestParam(defaultValue = "pending") String tab) {
        
        List<Pesanan> dataTampil;

        if (tab.equals("dibatalkan")) {
            // TAB 2: Tampilkan yang DIBATALKAN atau PEMBAYARAN_DITOLAK
            dataTampil = pesananRepo.findAll();
            dataTampil.removeIf(p -> 
                !(p.getStatusPesanan() == StatusPesanan.DIBATALKAN)
            );
        } 
        else if (tab.equals("riwayat")) {
            // TAB 3: Tampilkan yang BERHASIL (Dibayar / Selesai / Dikirim)
            dataTampil = pesananRepo.findAll();
            dataTampil.removeIf(p -> 
                !(p.getStatusPesanan() == StatusPesanan.DIBAYAR || 
                  p.getStatusPesanan() == StatusPesanan.DIKIRIM || 
                  p.getStatusPesanan() == StatusPesanan.SELESAI)
            );
        } 
        else {
            // TAB 1 (DEFAULT): Yang butuh verifikasi
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
                // Kalau diterima -> Masuk ke Flow Sukses (Tab Riwayat)
                p.setStatusPesanan(StatusPesanan.DIBAYAR); 
            } else {
                // Kalau ditolak -> Masuk ke Flow Batal (Tab Dibatalkan)
                p.setStatusPesanan(StatusPesanan.DIBATALKAN); 
            }
            pesananRepo.save(p);
        }
        // Redirect balik ke tab pending biar bisa lanjut kerja
        return "redirect:/admin/verifikasi-bayar?tab=pending";
    }

    // --- 4. FITUR VALIDASI IKLAN (Gabungan ValidasiIklanController) ---
    // --- 4. FITUR VALIDASI IKLAN (3 TAB: Pending, Active, Riwayat) ---
    @GetMapping("/validasi-iklan")
    public String halamanValidasiIklan(Model model, 
                                       @RequestParam(defaultValue = "pending") String tab) {
        
        List<BannerIklan> semuaIklan = bannerRepo.findAll();
        LocalDate hariIni = LocalDate.now();

        // LOGIKA FILTERING
        if (tab.equals("active")) {
            // TAMPILKAN: Yang Status ACTIVE DAN Tanggal Selesai >= Hari Ini
            semuaIklan.removeIf(i -> 
                !(i.getStatus() == StatusIklan.ACTIVE && 
                 (i.getTanggalSelesai().isAfter(hariIni) || i.getTanggalSelesai().isEqual(hariIni)))
            );

        } else if (tab.equals("riwayat")) {
            // TAMPILKAN: Yang DITOLAK, EXPIRED, atau ACTIVE tapi sudah lewat tanggal
            semuaIklan.removeIf(i -> 
                !(i.getStatus() == StatusIklan.REJECTED || 
                  i.getStatus() == StatusIklan.EXPIRED ||
                 (i.getStatus() == StatusIklan.ACTIVE && i.getTanggalSelesai().isBefore(hariIni)))
            );

        } else {
            // DEFAULT (PENDING): Tampilkan yang belum diproses
            semuaIklan.removeIf(i -> i.getStatus() != StatusIklan.PENDING);
        }
        
        model.addAttribute("listIklan", semuaIklan);
        model.addAttribute("activeTab", tab); // Kunci buat switch tab di HTML
        
        return "admin/validasi-iklan";
    }

    @PostMapping("/validasi-iklan/proses")
    public String prosesIklan(@RequestParam Long iklanId, @RequestParam String aksi) {
        BannerIklan iklan = bannerRepo.findById(iklanId).orElse(null);
        
        if (iklan != null) {
            if (aksi.equals("TERIMA")) {
                // 1. Update Status Iklan
                iklan.setStatus(StatusIklan.ACTIVE);
                iklan.setTanggalMulai(LocalDate.now());
                
                // 2. Hitung Tanggal Selesai otomatis (Pake Enum JenisPaket)
                int durasi = iklan.getJenisPaket().getDurasiHari();
                iklan.setTanggalSelesai(LocalDate.now().plusDays(durasi));
                
                // 3. Update Produk biar muncul di depan
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