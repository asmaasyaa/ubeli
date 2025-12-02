package com.ubeli.repository;

import com.ubeli.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // Tampilkan wishlist milik Pembeli A
    List<Wishlist> findByPembeli_PembeliId(Long pembeliId);
    
    // Cek apakah produk X sudah ada di wishlist user (biar gak dobel)
    boolean existsByPembeli_PembeliIdAndProduk_ProdukId(Long pembeliId, Long produkId);
}