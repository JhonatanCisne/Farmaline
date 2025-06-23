package com.Farmaline.Farmaline.repository;

import java.math.BigDecimal; // ¡Importante! Añadir esta importación
import java.time.LocalDate;
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

    // --- CAMBIO AQUÍ: Float a BigDecimal ---
    List<Producto> findByPrecioBetween(BigDecimal minPrecio, BigDecimal maxPrecio);

    List<Producto> findByNombreContainingIgnoreCaseAndDescripcionContainingIgnoreCase(String nombreTerm, String descripcionTerm);
    Optional<Producto> findByNombreIgnoreCase(String nombre);
    Optional<Producto> findByNombre(String nombre);

    // --- CAMBIO AQUÍ: float a BigDecimal ---
    List<Producto> findByPrecioLessThanEqual(BigDecimal precio);

    long countByStockDisponibleLessThan(Integer stock);

    List<Producto> findByStockDisponibleLessThanEqual(Integer stock);

    List<Producto> findByFechaCaducidadBefore(LocalDate fecha);

    List<Producto> findByFechaIngresoAfter(LocalDate fecha);

    // --- CAMBIO AQUÍ: Float a BigDecimal ---
    List<Producto> findByIgvBetween(BigDecimal minIgv, BigDecimal maxIgv);

    // --- CAMBIO AQUÍ: Float a BigDecimal ---
    List<Producto> findByPrecioFinalBetween(BigDecimal minPrecioFinal, BigDecimal maxPrecioFinal);

    // --- CAMBIO AQUÍ: Float a BigDecimal ---
    long countByPrecioBetween(BigDecimal minPrecio, BigDecimal maxPrecio);

    List<Producto> findAllByOrderByNombreAsc();

    List<Producto> findAllByOrderByPrecioDesc();
}