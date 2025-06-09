package com.Farmaline.Farmaline.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Farmaline.Farmaline.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByDescripcionContainingIgnoreCase(String descripcion);

    List<Producto> findByStockDisponibleGreaterThan(Integer stock);

    List<Producto> findByPrecioBetween(Float minPrecio, Float maxPrecio);

    List<Producto> findByNombreContainingIgnoreCaseAndDescripcionContainingIgnoreCase(String nombreTerm, String descripcionTerm);

    Optional<Producto> findByNombreIgnoreCase(String nombre);

    Optional<Producto> findByNombre(String nombre);

    List<Producto> findByPrecioLessThanEqual(float precio); 

    long countByStockDisponibleLessThan(Integer stock);
}