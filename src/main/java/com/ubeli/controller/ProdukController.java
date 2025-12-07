package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
// import org.springframework.beans.factory.annotation.Autowired;
// import com.ubeli.repository.ProdukRepository;
// import com.ubeli.entity.Produk;
// import com.ubeli.entity.Kategori;

@Controller
@RequestMapping("/produk") 
public class ProdukController {

    // -----------------------------------------
    // @Autowired
    // private ProdukRepository produkRepository;

    // @Autowired
    // private KategoriRepository kategoriRepository; 
    // -----------------------------------------

    // ==== HALAMAN TAMBAH PRODUK (Jual Barang) ====
    @GetMapping("/tambah-produk")
    public String halamanTambahProduk(Model model) {

        // Jika kamu sudah punya kategori di database → tampilkan di dropdown
        // model.addAttribute("kategoriList", kategoriRepository.findAll());

        return "penjual/tambah-produk"; 
    }
    @PostMapping("/tambah-produk")
    public String prosesTambahProduk(
            @RequestParam("namaProduk") String namaProduk,
            @RequestParam("harga") Long harga,
            @RequestParam("kategori") String kategori,
            @RequestParam("kondisi") String kondisi,
            @RequestParam("deskripsi") String deskripsi
            // @RequestParam("file") MultipartFile file
    ) {

        // Contoh struktur penyimpanan nanti:
        /*
        Produk p = new Produk();
        p.setNamaProduk(namaProduk);
        p.setHarga(harga);
        p.setKondisi(kondisi);
        p.setDeskripsi(deskripsi);
        p.setKategori(kategoriRepository.findById(kategori).get());

        produkRepository.save(p);
        */
        return "redirect:/"; // balik ke homepage setelah submit
    }
}