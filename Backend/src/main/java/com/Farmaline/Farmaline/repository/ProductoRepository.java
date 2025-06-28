package com.farmaline.farmaline.repository;

import java.time.LocalDate;
import java.util.List; // Importa esta anotación

import org.springframework.data.jpa.repository.JpaRepository; // Importa esta anotación
import org.springframework.data.jpa.repository.Query; // Asegúrate de que el paquete sea el correcto
import org.springframework.data.repository.query.Param; // Importa LocalDate

import com.farmaline.farmaline.model.Producto; // Importa List

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // Este es el nuevo método para filtrar productos de manera dinámica
    @Query("SELECT p FROM Producto p WHERE " +
           "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:stockMinimo IS NULL OR p.stockDisponible >= :stockMinimo) AND " +
           "(:stockMaximo IS NULL OR p.stockDisponible <= :stockMaximo) AND " +
           "(:fechaCaducidadHasta IS NULL OR p.fechaCaducidad <= :fechaCaducidadHasta)")
    List<Producto> findProductosByFilters(
            @Param("nombre") String nombre,
            @Param("stockMinimo") Integer stockMinimo,
            @Param("stockMaximo") Integer stockMaximo,
            @Param("fechaCaducidadHasta") LocalDate fechaCaducidadHasta);
}