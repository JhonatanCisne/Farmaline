package com.farmaline.farmaline.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmaline.farmaline.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}