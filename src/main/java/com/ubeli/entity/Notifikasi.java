package com.ubeli.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notifikasi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   

    private String judul;

    private String subJudul;

    private String waktu;

    private String status;

    // RELASI PENERIMA NOTIFIKASI
    @ManyToOne
    @JoinColumn(name = "pembeli_id")
    private Pembeli pembeli;

    @ManyToOne
    @JoinColumn(name = "penjual_id")
    private Penjual penjual;

    @ManyToOne
    @JoinColumn(name="pesanan_id")
    private Pesanan pesanan;

}
