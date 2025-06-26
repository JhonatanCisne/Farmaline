package com.farmaline.farmaline.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmaline.farmaline.model.Vehiculo;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {
}