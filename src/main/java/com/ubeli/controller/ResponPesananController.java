package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ubeli.entity.Pesanan;
import com.ubeli.service.PesananService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor

public class ResponPesananController {

    private final PesananService pesananService;

    @PostMapping("/ajukan/{produkId}")
    public String ajukanPembelian(@PathVariable Long produkId,
                                  @RequestParam Long pembeliId) {

        pesananService.ajukanPembelian(produkId, pembeliId);

        return "redirect:/produk/" + produkId + "?success=ajuan-dikirim";
    }

    @PostMapping("/terima/{pesananId}")
    public String terimaPengajuan(@PathVariable Long pesananId) {

        Pesanan pesanan = pesananService.terimaPengajuan(pesananId);
        Long produkId = pesanan.getProduk().getProdukId();

        return "redirect:/penjual/produk/" + produkId + "?success=ajuan-diterima";
    }
}