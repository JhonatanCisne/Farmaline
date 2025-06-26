package com.farmaline.farmaline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmaline.farmaline.model.Carrito_Anadido;

public interface CarritoAnadidoRepository extends JpaRepository<Carrito_Anadido, Integer> {
    List<Carrito_Anadido> findByCarritoIdCarrito(Integer idCarrito);
    void deleteByCarritoIdCarrito(Integer idCarrito);
}