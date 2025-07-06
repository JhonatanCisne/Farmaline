package com.farmaline.farmaline.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmaline.farmaline.model.LoteProducto;

@Repository
public interface LoteProductoRepository extends JpaRepository<LoteProducto, Integer> {

    List<LoteProducto> findByProducto_IdProductoOrderByFechaCaducidadAsc(Integer idProducto);

    Optional<LoteProducto> findByProducto_IdProductoAndNumeroLote(Integer idProducto, String numeroLote);

    void deleteByProducto_IdProducto(Integer idProducto);

    List<LoteProducto> findByFechaCaducidadBefore(LocalDate fecha);
}