package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;

import com.ubeli.entity.Produk;
import com.ubeli.entity.Kategori;
import com.ubeli.entity.Penjual;
import com.ubeli.entity.FotoProduk;

import com.ubeli.repository.ProdukRepository;
import com.ubeli.repository.KategoriRepository;
import com.ubeli.repository.PenjualRepository;
import com.ubeli.repository.FotoProdukRepository;

import java.math.BigDecimal;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/penjual")
public class ProdukController {

    @Autowired
    private ProdukRepository produkRepo;

    @Autowired
    private KategoriRepository kategoriRepo;

    @Autowired
    private PenjualRepository penjualRepo;

    @Autowired
    private FotoProdukRepository fotoRepo;

    @GetMapping("/tambah-produk")
    public String halamanTambahProduk() {
        return "penjual/tambah-produk";
    }

    @PostMapping("/tambah-produk")
    public String prosesTambahProduk(
            @RequestParam("kategoriId") Long kategoriId,
            @RequestParam("merk") String merk,
            @RequestParam("namaProduk") String namaProduk,
            @RequestParam("kondisi") String kondisi,
            @RequestParam("harga") BigDecimal harga,
            @RequestParam("deskripsi") String deskripsi,
            @RequestParam("file") MultipartFile[] files,
            HttpSession session,
            RedirectAttributes ra
    ) {

        // ========== SIMPAN PRODUK ==========
        Produk p = new Produk();
        p.setNamaProduk(namaProduk);
        p.setDeskripsi(deskripsi);
        p.setHarga(harga);
        p.setStatus("Available");
        p.setDiiklankan(false);
        p.setMerk(merk);
        p.setKondisi(kondisi);

        // kategori
        Kategori kategori = kategoriRepo.findById(kategoriId).orElse(null);
        p.setKategori(kategori);

        Penjual penjual = (Penjual) session.getAttribute("penjual");
        if (penjual == null) {
            return "redirect:/login";
        }
        p.setPenjual(penjual);

        

        Produk savedProduk = produkRepo.save(p);

        // ========== SIMPAN FOTO PRODUK ==========
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                FotoProduk foto = new FotoProduk();
                foto.setProduk(savedProduk);
                foto.setUrlFoto(file.getOriginalFilename()); // nanti diganti stored filename
                fotoRepo.save(foto);
            }
        }

        // ========== KIRIM SINYAL SUKSES KE HTML ==========
        ra.addFlashAttribute("success", true);

        return "redirect:/penjual/profil";
    }

    @PostMapping("/hapus-produk/{id}")
    public String hapusProduk(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes ra
    ) {
        // Pastikan penjual login
        Penjual penjual = (Penjual) session.getAttribute("penjual");
        if (penjual == null) {
            return "redirect:/login";
        }

        // Ambil produk berdasarkan ID
        Produk produk = produkRepo.findById(id).orElse(null);
        if (produk == null) {
            ra.addFlashAttribute("error", "Produk tidak ditemukan.");
            return "redirect:/penjual/profil";
        }

        // Cek apakah produk milik penjual yang sedang login
        if (!produk.getPenjual().getPenjualId().equals(penjual.getPenjualId())) {
            ra.addFlashAttribute("error", "Anda tidak memiliki izin untuk menghapus produk ini.");
            return "redirect:/penjual/profil";
        }

        // Hapus produk (otomatis hapus foto karena cascade = ALL)
        produkRepo.delete(produk);

        ra.addFlashAttribute("successDelete", "Produk berhasil dihapus!");

        return "redirect:/penjual/profil"; // Setelah hapus balik ke profil penjual
    }
}