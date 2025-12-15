package com.ubeli.repository;

import com.ubeli.entity.Item;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByPesanan_PesananId(Long pesananId);

}