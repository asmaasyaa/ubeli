package com.ubeli;

// import java.math.BigDecimal;
// import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ubeli.entity.*;
import com.ubeli.repository.*;
//import com.ubeli.enums.*;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private AdminRepository adminRepo;
    // @Autowired private ProdukRepository produkRepo;
    // @Autowired private PenjualRepository penjualRepo;
    // @Autowired private PembeliRepository pembeliRepo;
    // @Autowired private KategoriRepository kategoriRepo;
    // @Autowired private PesananRepository pesananRepo;
    // @Autowired private BannerIklanRepository bannerRepo;
    // @Autowired private LaporanRepository laporanRepo;

    @Override
    public void run(String... args) throws Exception {
        
        // 1. BUAT AKUN ADMIN (Cek dulu biar gak dobel)
        if (adminRepo.count() == 0) {
            Admin admin = new Admin();
            admin.setUsername("superadmin");
            admin.setPasswordHash("admin123");
            adminRepo.save(admin);
            System.out.println(">>> ADMIN DUMMY: superadmin / admin123 <<<");
        }

        // // 2. CEK APAKAH DATABASE PRODUK KOSONG? (Kalau kosong, isi semua data)
        // if (produkRepo.count() == 0) {
        //     System.out.println(">>> MEMULAI DATA SEEDING LENGKAP... <<<");

        //     // --- KATEGORI ---
        //     // CARA BARU (Benar - Pakai Setter):
        //     Kategori elektronik = new Kategori();
        //     elektronik.setNamaKategori("Elektronik");
        //     kategoriRepo.save(elektronik);

        //     Kategori fashion = new Kategori();
        //     fashion.setNamaKategori("Fashion Pria");
        //     kategoriRepo.save(fashion);

        //     Kategori hobi = new Kategori();
        //     hobi.setNamaKategori("Hobi & Koleksi");
        //     kategoriRepo.save(hobi);

        //     // --- PENJUAL ---
        //     Penjual tokoBudi = new Penjual();
        //     tokoBudi.setNamaLengkap("Toko Budi Jaya");
        //     tokoBudi.setEmail("budi@toko.com");
        //     tokoBudi.setPasswordHash("123");
        //     tokoBudi.setNoHp("08123456789");
        //     tokoBudi.setBank("BCA");
        //     tokoBudi.setNoRekening("1234567890");
        //     tokoBudi.setStatus("Active");
        //     penjualRepo.save(tokoBudi);

        //     Penjual tokoSiti = new Penjual();
        //     tokoSiti.setNamaLengkap("Siti Second Branded");
        //     tokoSiti.setEmail("siti@toko.com");
        //     tokoSiti.setPasswordHash("123");
        //     tokoSiti.setNoHp("08987654321");
        //     tokoSiti.setBank("Mandiri");
        //     tokoSiti.setNoRekening("0987654321");
        //     tokoSiti.setStatus("Active");
        //     penjualRepo.save(tokoSiti);

        //     // --- PEMBELI ---
        //     Pembeli userAndi = new Pembeli();
        //     userAndi.setNamaLengkap("Andi Pembeli");
        //     userAndi.setEmail("andi@test.com");
        //     userAndi.setPasswordHash("123");
        //     userAndi.setNoHp("08111222333");
        //     userAndi.setStatus("Active");
        //     pembeliRepo.save(userAndi);

        //     // --- PRODUK ---
        //     Produk p1 = new Produk();
        //     p1.setNamaProduk("Laptop ASUS ROG Bekas");
        //     p1.setHarga(new BigDecimal("8500000"));
        //     p1.setDeskripsi("RAM 16GB, SSD 512GB, Minus lecet pemakaian wajar.");
        //     p1.setStatus("Available");
        //     p1.setDiiklankan(true);
        //     p1.setPeriodeIklan(LocalDate.now().plusDays(3));
        //     p1.setPenjual(tokoBudi);
        //     p1.setKategori(elektronik);
        //     p1.tambahFoto("https://placehold.co/600x400/png?text=Laptop+ROG");
        //     produkRepo.save(p1);

        //     Produk p2 = new Produk();
        //     p2.setNamaProduk("Sepatu Nike Air Jordan");
        //     p2.setHarga(new BigDecimal("1200000"));
        //     p2.setDeskripsi("Size 42, Original, Box masih ada.");
        //     p2.setStatus("Available");
        //     p2.setDiiklankan(false);
        //     p2.setPenjual(tokoSiti);
        //     p2.setKategori(fashion);
        //     p2.tambahFoto("https://placehold.co/600x400/png?text=Sepatu+Nike");
        //     produkRepo.save(p2);

        //     Produk p3 = new Produk();
        //     p3.setNamaProduk("Gitar Akustik Yamaha");
        //     p3.setHarga(new BigDecimal("750000"));
        //     p3.setDeskripsi("Suara masih nyaring, senar baru diganti.");
        //     p3.setStatus("Available");
        //     p3.setDiiklankan(false);
        //     p3.setPenjual(tokoBudi);
        //     p3.setKategori(hobi);
        //     p3.tambahFoto("https://placehold.co/600x400/png?text=Gitar+Yamaha");
        //     produkRepo.save(p3);

        //     // ==========================================
        //     // DATA DUMMY KHUSUS TESTING ADMIN
        //     // ==========================================
        //     // Karena variabel userAndi, tokoBudi, p2, p3 masih ada di scope ini,
        //     // kita bisa pakai mereka langsung.

        //     // 1. DATA VERIFIKASI PEMBAYARAN
        //     Pesanan pVerif = new Pesanan();
        //     pVerif.setPembeli(userAndi);
        //     pVerif.setPenjual(tokoBudi);
        //     pVerif.setTotalHarga(new BigDecimal("150000"));
        //     pVerif.setStatusPesanan(StatusPesanan.VERIFIKASI_ADMIN); 
        //     pVerif.setBuktiTransferUrl("https://placehold.co/400x600/png?text=Bukti+Transfer");
        //     pesananRepo.save(pVerif);

        //     // 2. DATA VALIDASI IKLAN (Boost Sepatu)
        //     BannerIklan iklanBaru = new BannerIklan();
        //     iklanBaru.setProduk(p2); // Sepatu Nike
        //     iklanBaru.setJenisPaket(JenisPaket.PAKET_7_HARI);
        //     iklanBaru.setStatus(StatusIklan.PENDING);
        //     bannerRepo.save(iklanBaru);

        //     // 3. DATA LAPORAN (Lapor Gitar Rusak)
        //     Laporan lapor = new Laporan();
        //     lapor.setPelapor(userAndi);
        //     lapor.setTerlapor(tokoBudi); // Melaporkan Toko Budi (karena gitarnya dari dia)
        //     lapor.setProduk(p3); // Gitar
        //     lapor.setAlasan("Barang tidak sesuai deskripsi, lecet parah.");
        //     lapor.setStatus("Pending");
        //     laporanRepo.save(lapor);

        //     System.out.println(">>> SUKSES! SEMUA DATA DUMMY (USER + PRODUK + ADMIN) SIAP! <<<");
        // } else {
        //     System.out.println(">>> Database sudah ada isinya, skip seeding. <<<");
        // }
    }
}