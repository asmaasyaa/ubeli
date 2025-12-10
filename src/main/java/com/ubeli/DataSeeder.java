package com.ubeli;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ubeli.entity.*;
import com.ubeli.repository.*;
import com.ubeli.enums.*;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private AdminRepository adminRepo;
    @Autowired private ProdukRepository produkRepo;
    @Autowired private PenjualRepository penjualRepo;
    @Autowired private PembeliRepository pembeliRepo;
    @Autowired private KategoriRepository kategoriRepo;
    @Autowired private PesananRepository pesananRepo;
    @Autowired private BannerIklanRepository bannerRepo;
    @Autowired private LaporanRepository laporanRepo;

    @Override
    public void run(String... args) throws Exception {
        
        // Cek dulu: Kalau database Produk masih kosong, baru kita isi.
        // (Supaya data gak numpuk/dobel tiap kali aplikasi di-restart)
        if (produkRepo.count() == 0) {
            
            System.out.println(">>> MEMULAI DATA SEEDING... <<<");

            // 1. BUAT KATEGORI
            Kategori elektronik = new Kategori();
            elektronik.setNamaKategori("Elektronik");
            kategoriRepo.save(elektronik);

            Kategori fashion = new Kategori();
            fashion.setNamaKategori("Fashion Pria");
            kategoriRepo.save(fashion);

            Kategori hobi = new Kategori();
            hobi.setNamaKategori("Hobi & Koleksi");
            kategoriRepo.save(hobi);

            // 2. BUAT PENJUAL DUMMY (Akun Toko)
            Penjual tokoBudi = new Penjual();
            tokoBudi.setNamaLengkap("Toko Budi Jaya");
            tokoBudi.setEmail("budi@toko.com");
            tokoBudi.setPasswordHash("123"); // Password dummy
            tokoBudi.setNoHp("08123456789");
            tokoBudi.setBank("BCA");
            tokoBudi.setNoRekening("1234567890");
            tokoBudi.setStatus("Active");
            penjualRepo.save(tokoBudi);

            Penjual tokoSiti = new Penjual();
            tokoSiti.setNamaLengkap("Siti Second Branded");
            tokoSiti.setEmail("siti@toko.com");
            tokoSiti.setPasswordHash("123");
            tokoSiti.setNoHp("08987654321");
            tokoSiti.setBank("Mandiri");
            tokoSiti.setNoRekening("0987654321");
            tokoSiti.setStatus("Active");
            penjualRepo.save(tokoSiti);

            // 3. BUAT PEMBELI DUMMY (Buat Test Login)
            Pembeli userAndi = new Pembeli();
            userAndi.setNamaLengkap("Andi Pembeli");
            userAndi.setEmail("andi@test.com"); // Nanti login pake ini
            userAndi.setPasswordHash("123");     // Passwordnya ini
            userAndi.setNoHp("08111222333");
            userAndi.setStatus("Active");
            pembeliRepo.save(userAndi);

            // 4. BUAT PRODUK DUMMY (Barang Dagangan)
            
            // Produk 1: Laptop
            Produk p1 = new Produk();
            p1.setNamaProduk("Laptop ASUS ROG Bekas");
            p1.setHarga(new BigDecimal("8500000"));
            p1.setDeskripsi("RAM 16GB, SSD 512GB, Minus lecet pemakaian wajar. Nego tipis.");
            p1.setStatus("Available");
            p1.setDiiklankan(true); // Ceritanya lagi di-boost
            p1.setPeriodeIklan(LocalDate.now().plusDays(3));
            p1.setPenjual(tokoBudi);
            p1.setKategori(elektronik);
            produkRepo.save(p1);

            // Produk 2: Sepatu
            Produk p2 = new Produk();
            p2.setNamaProduk("Sepatu Nike Air Jordan");
            p2.setHarga(new BigDecimal("1200000"));
            p2.setDeskripsi("Size 42, Original, Box masih ada. Jarang dipakai.");
            p2.setStatus("Available");
            p2.setDiiklankan(false);
            p2.setPenjual(tokoSiti);
            p2.setKategori(fashion);
            produkRepo.save(p2);

            // Produk 3: Gitar
            Produk p3 = new Produk();
            p3.setNamaProduk("Gitar Akustik Yamaha");
            p3.setHarga(new BigDecimal("750000"));
            p3.setDeskripsi("Suara masih nyaring, senar baru diganti.");
            p3.setStatus("Available");
            p3.setDiiklankan(false);
            p3.setPenjual(tokoBudi);
            p3.setKategori(hobi);
            produkRepo.save(p3);

            System.out.println(">>> SUKSES! DATA DUMMY TELAH DITAMBAHKAN KE DATABASE <<<");
        } else {
            System.out.println(">>> Database sudah ada isinya, skip seeding. <<<");
        }
    }
}