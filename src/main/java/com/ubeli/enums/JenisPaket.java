package com.ubeli.enums;

import java.math.BigDecimal;

public enum JenisPaket {
    // Definisi Paket sesuai UI: (Durasi, Harga)
    PAKET_1_HARI(1, new BigDecimal("20000")),
    PAKET_3_HARI(3, new BigDecimal("50000")),
    PAKET_7_HARI(7, new BigDecimal("100000"));

    private final int durasiHari;
    private final BigDecimal harga;

    JenisPaket(int durasiHari, BigDecimal harga) {
        this.durasiHari = durasiHari;
        this.harga = harga;
    }

    public int getDurasiHari() {
        return durasiHari;
    }

    public BigDecimal getHarga() {
        return harga;
    }

    public String getLabel() {
    return "Paket " + durasiHari + " Hari";
}

}