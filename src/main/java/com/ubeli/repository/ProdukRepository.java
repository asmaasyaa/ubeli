package com.ubeli.repository;

import com.ubeli.entity.Produk;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProdukRepository extends JpaRepository<Produk, Long> {
    
    // Fitur Search: Cari produk berdasarkan nama (mirip SQL LIKE %nama%)
    List<Produk> findByNamaProdukContaining(String keyword);

    // Fitur Iklan: Cari semua produk yang sedang di-boost
    List<Produk> findByDiiklankanTrue();
    
    // Fitur Kategori: Cari produk berdasarkan ID kategori
    List<Produk> findByKategori_KategoriId(Long kategoriId);
    
    // Fitur Toko: Cari semua produk milik penjual tertentu
    List<Produk> findByPenjual_PenjualId(Long penjualId);
}