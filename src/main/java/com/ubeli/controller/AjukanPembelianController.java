package com.ubeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ubeli.entity.Pesanan;
import com.ubeli.service.PesananService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AjukanPembelianController {

    private final PesananService pesananService;

    @PostMapping("/ajukan/{produkId}")
    public String ajukanPembelian(@PathVariable Long produkId,
                                  @RequestParam Long pembeliId) {

        try {
            pesananService.ajukanPembelian(produkId, pembeliId);
            return "redirect:/produk/" + produkId + "?success=ajuan-dikirim";
        } catch (RuntimeException e) {
            // encode message minimal (jika ada spasi/dll pada message)
            String msg = e.getMessage() != null ? e.getMessage().replaceAll("\\s+", "%20") : "error";
            return "redirect:/produk/" + produkId + "?error=" + msg;
        }
    }

    @GetMapping("/terima/{pesananId}")
    public String terimaPengajuan(@PathVariable Long pesananId) {

        Pesanan pesanan = pesananService.terimaPengajuan(pesananId);
        return "redirect:/notifikasi?success=diterima";
    }

    @GetMapping("/tolak/{pesananId}")
    public String tolakPengajuan(@PathVariable Long pesananId) {

        pesananService.tolakPengajuan(pesananId);
        return "redirect:/notifikasi?success=ditolak";
    }
}
