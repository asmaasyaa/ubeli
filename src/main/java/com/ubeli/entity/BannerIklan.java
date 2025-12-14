package com.ubeli.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import com.ubeli.enums.StatusIklan; 
import com.ubeli.enums.JenisPaket; 

@Entity
@Data
@Table(name = "banner_iklan")
public class BannerIklan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iklanId; 

    // RELASI KE PRODUK
    @ManyToOne
    @JoinColumn(name = "produk_id", nullable = false)
    private Produk produk;

    // ENUM STATUS 
    @Enumerated(EnumType.STRING) 
    private StatusIklan status; 

    // ENUM PAKET 
    @Enumerated(EnumType.STRING)
    private JenisPaket jenisPaket;

    // TANGGAL
    private LocalDate tanggalMulai;   
    private LocalDate tanggalSelesai; 
}