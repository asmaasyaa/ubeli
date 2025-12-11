package com.ubeli.repository;

import com.ubeli.entity.Notifikasi;
import com.ubeli.entity.Pembeli;
import com.ubeli.entity.Penjual;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotifikasiRepository extends JpaRepository<Notifikasi, Long> {

    // Notifikasi untuk pembeli
    List<Notifikasi> findByPembeli_PembeliId(Long pembeliId);

    // Notifikasi untuk penjual
    List<Notifikasi> findByPenjual_PenjualId(Long penjualId);
}
