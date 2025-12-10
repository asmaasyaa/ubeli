package com.ubeli.enums;

import java.math.BigDecimal;

public enum JenisPaket {
    // Definisi sesuai UI kamu:
    PAKET_1_HARI(1, new BigDecimal("20000"), "1 Hari"),
    PAKET_3_HARI(3, new BigDecimal("50000"), "3 Hari"),
    PAKET_7_HARI(7, new BigDecimal("100000"), "7 Hari");

    private final int durasiHari;
    private final BigDecimal harga;
    private final String label;

    JenisPaket(int durasiHari, BigDecimal harga, String label) {
        this.durasiHari = durasiHari;
        this.harga = harga;
        this.label = label;
    }

    public int getDurasiHari() { return durasiHari; }
    public BigDecimal getHarga() { return harga; }
    public String getLabel() { return label; }
}