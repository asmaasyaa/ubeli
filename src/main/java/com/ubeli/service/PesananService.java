package com.ubeli.service;

import com.ubeli.entity.Pesanan;

public interface PesananService {

    Pesanan ajukanPembelian(Long produkId, Long pembeliId);

    Pesanan terimaPengajuan(Long pesananId);

    void tolakPengajuan(Long pesananId);

}
