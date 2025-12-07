package com.ubeli.controller;

import com.ubeli.entity.Laporan;
import com.ubeli.entity.Pembeli;
import com.ubeli.entity.Penjual;
import com.ubeli.entity.Produk;
import com.ubeli.repository.LaporanRepository;
import com.ubeli.repository.ProdukRepository;
import com.ubeli.repository.PembeliRepository;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class BuatLaporanController {

    @Autowired
    private LaporanRepository laporanRepository;

    @Autowired
    private ProdukRepository produkRepository;

    @Autowired
    private PembeliRepository pembeliRepository;

    @GetMapping("/buat")
    public String buatLaporanPage() {
        return "pembeli/buat-laporan";
    }

@PostMapping("/laporan/kirim")
public String kirimLaporan(
        @RequestParam("alasan") String alasan,
        @RequestParam("tanggalKejadian") String tanggalKejadian,
        @RequestParam("deskripsi") String deskripsi,
        @RequestParam("produkId") Long produkId,
        @RequestParam("bukti") MultipartFile bukti
) {

    Produk produk = produkRepository.findById(produkId).orElse(null);
    if (produk == null) {
        return "redirect:/laporan/buat-laporan/" + produkId + "?error=produk";
    }

    Pembeli pelapor = pembeliRepository.findById(1L).orElse(null);
    Penjual terlapor = produk.getPenjual();

    String buktiUrl = "/uploads/" + bukti.getOriginalFilename();

    Laporan laporan = new Laporan();
    laporan.setAlasan(alasan);
    laporan.setTanggalKejadian(LocalDate.parse(tanggalKejadian));
    laporan.setDeskripsi(deskripsi);
    laporan.setBuktiFotoUrl(buktiUrl);
    laporan.setStatus("Pending");
    laporan.setPelapor(pelapor);
    laporan.setTerlapor(terlapor);
    laporan.setProduk(produk);

    laporanRepository.save(laporan);

    return "redirect:/laporan/buat-laporan/" + produkId + "?success";
    }
}