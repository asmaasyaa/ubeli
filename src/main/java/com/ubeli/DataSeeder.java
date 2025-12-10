package com.ubeli;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

// Import semua Entity, Repository, dan Enum
import com.ubeli.entity.*;
import com.ubeli.repository.*;
import com.ubeli.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    // Panggil semua Repository yang mau diisi
    @Autowired private ProdukRepository produkRepo;
    @Autowired private PenjualRepository penjualRepo;
    @Autowired private PembeliRepository pembeliRepo;
    @Autowired private KategoriRepository kategoriRepo;

    @Override
    public void run(String... args) throws Exception {
        
        // Cek dulu: Kalau database Produk masih kosong, baru kita isi.
        // (Supaya data gak numpuk/dobel tiap kali aplikasi di-restart)
    }
}