package com.ubeli.repository;

import com.ubeli.entity.Notifikasi;
import com.ubeli.entity.Pembeli;
import com.ubeli.entity.Penjual;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface NotifikasiRepository extends JpaRepository<Notifikasi, Long> {

    // Notifikasi untuk pembeli
    List<Notifikasi> findByPembeli_PembeliId(Long pembeliId);

    // Notifikasi untuk penjual
    List<Notifikasi> findByPenjual_PenjualId(Long penjualId);

    Optional<Notifikasi> findByPesanan_PesananId(Long pesananId);

    // Ambil notifikasi dari pembeli lain yang satu produk (untuk ditolak)
    List<Notifikasi> findByPesanan_Produk_ProdukIdAndPesanan_PesananIdNot(Long produkId, Long pesananId);

    Notifikasi findFirstByPesanan_PesananIdOrderByIdDesc(Long pesananId);

}
