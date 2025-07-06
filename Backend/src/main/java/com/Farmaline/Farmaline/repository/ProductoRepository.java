package com.farmaline.farmaline.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmaline.farmaline.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByLaboratorioContainingIgnoreCase(String laboratorio);

    List<Producto> findByPrecioBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Producto> findByNombreContainingIgnoreCaseAndLaboratorioContainingIgnoreCase(String nombre, String laboratorio);

    boolean existsByNombreIgnoreCase(String nombre);
}