package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import com.ubeli.entity.*;
import com.ubeli.repository.*;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.nio.file.*;
import java.util.UUID;

@Controller
@RequestMapping("/penjual")
public class ProdukController {

    @Autowired
    private ProdukRepository produkRepo;

    @Autowired
    private KategoriRepository kategoriRepo;

    @Autowired
    private FotoProdukRepository fotoRepo;

    /* ===============================
       TAMBAH PRODUK
    ================================= */

    @GetMapping("/tambah-produk")
    public String halamanTambahProduk() {
        return "penjual/tambah-produk";
    }

    @PostMapping("/tambah-produk")
    public String prosesTambahProduk(
            @RequestParam Long kategoriId,
            @RequestParam String merk,
            @RequestParam String namaProduk,
            @RequestParam String kondisi,
            @RequestParam BigDecimal harga,
            @RequestParam String deskripsi,
            @RequestParam(value = "file", required = false) MultipartFile[] files,
            HttpSession session
    ) {

        System.out.println("JUMLAH FILE = " + files.length);

        Penjual penjual = (Penjual) session.getAttribute("penjual");
        if (penjual == null) return "redirect:/login";

        Produk p = new Produk();
        p.setNamaProduk(namaProduk);
        p.setDeskripsi(deskripsi);
        p.setHarga(harga);
        p.setStatus("Available");
        p.setDiiklankan(false);
        p.setMerk(merk);
        p.setKondisi(kondisi);

        p.setKategori(kategoriRepo.findById(kategoriId).orElse(null));
        p.setPenjual(penjual);

        Produk savedProduk = produkRepo.save(p);

        System.out.println("JUMLAH FILE: " + files.length);
        for (MultipartFile f : files) {
            System.out.println("FILE: " + f.getOriginalFilename() + " | empty=" + f.isEmpty());
        }
        // ===== SIMPAN FOTO =====
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {

                String namaFile = UUID.randomUUID() + "_" + file.getOriginalFilename();

                FotoProduk foto = new FotoProduk();
                foto.setUrlFoto("/img/produk/" + namaFile);
                foto.setProduk(p);

                p.getListFoto().add(foto);
            }
        }
        produkRepo.save(p);

        return "redirect:/penjual/profil";
    }


    /* ===============================
       EDIT PRODUK
    ================================= */

    @GetMapping("/edit-produk/{id}")
    public String halamanEditProduk(
            @PathVariable Long id,
            Model model,
            HttpSession session
    ) {

        Penjual penjual = (Penjual) session.getAttribute("penjual");
        if (penjual == null) return "redirect:/login";

        Produk produk = produkRepo.findById(id).orElse(null);
        if (produk == null ||
            !produk.getPenjual().getPenjualId().equals(penjual.getPenjualId())) {
            return "redirect:/penjual/profil";
        }

        model.addAttribute("produk", produk);
        model.addAttribute("mode", "edit");

        return "penjual/tambah-produk";
    }

    @PostMapping("/edit-produk/{id}")

    public String prosesEditProduk(
            @PathVariable Long id,
            @RequestParam Long kategoriId,
            @RequestParam String merk,
            @RequestParam String namaProduk,
            @RequestParam String kondisi,
            @RequestParam BigDecimal harga,
            @RequestParam String deskripsi,
            @RequestParam(value = "file", required = false) MultipartFile[] files,
            HttpSession session
    ) {
        System.out.println("=== EDIT PRODUK MASUK ===");

        Penjual penjual = (Penjual) session.getAttribute("penjual");
        if (penjual == null) return "redirect:/login";

        Produk p = produkRepo.findById(id).orElse(null);
        if (p == null ||
            !p.getPenjual().getPenjualId().equals(penjual.getPenjualId())) {
            return "redirect:/penjual/profil";
        }

        p.setMerk(merk);
        p.setNamaProduk(namaProduk);
        p.setKondisi(kondisi);
        p.setHarga(harga);
        p.setDeskripsi(deskripsi);

        Kategori kategori = kategoriRepo.findById(kategoriId).orElse(null);
        p.setKategori(kategori);

        produkRepo.save(p);
        
        if (files != null) {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                FotoProduk foto = new FotoProduk();
                foto.setProduk(p);
                foto.setUrlFoto(file.getOriginalFilename());
                fotoRepo.save(foto);
            }
        }
    }
            return "redirect:/penjual/profil";
        }


    /* ===============================
       HAPUS PRODUK
    ================================= */

    @PostMapping("/hapus-produk/{id}")
    public String hapusProduk(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes ra
    ) {

        Penjual penjual = (Penjual) session.getAttribute("penjual");
        if (penjual == null) return "redirect:/login";

        Produk produk = produkRepo.findById(id).orElse(null);
        if (produk == null ||
            !produk.getPenjual().getPenjualId().equals(penjual.getPenjualId())) {
            ra.addFlashAttribute("error", "Tidak punya akses");
            return "redirect:/penjual/profil";
        }

        produkRepo.delete(produk);
        ra.addFlashAttribute("successDelete", true);

        return "redirect:/penjual/profil";
    }

    /* ===============================
       UTIL UPLOAD FOTO (SATU SUMBER)
    ================================= */

    private void simpanFotoProduk(Produk produk, MultipartFile[] files) throws Exception {

        if (files == null || files.length == 0) return;

        Path uploadDir = Paths.get("src/main/resources/static/img/produk/");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {

                String namaFile = UUID.randomUUID() + "_" + file.getOriginalFilename();

                Files.copy(
                    file.getInputStream(),
                    uploadDir.resolve(namaFile),
                    StandardCopyOption.REPLACE_EXISTING
                );

                FotoProduk foto = new FotoProduk();
                foto.setProduk(produk);
                foto.setUrlFoto("/img/produk/" + namaFile);
                fotoRepo.save(foto);
            }
        }
    }
}
