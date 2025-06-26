package com.farmaline.farmaline.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmaline.farmaline.model.Carrito;

public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
    Optional<Carrito> findByUsuarioIdUsuario(Integer idUsuario);
}