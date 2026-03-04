package com.pelanggan.pelanggan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pelanggan.pelanggan.model.Pelanggan;;

@Repository
public interface PelangganRepository extends JpaRepository<Pelanggan, Long>{

}
