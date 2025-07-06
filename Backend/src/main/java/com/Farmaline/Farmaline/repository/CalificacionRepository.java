package com.farmaline.farmaline.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.model.Calificacion;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Integer> {

    List<Calificacion> findByProducto_IdProducto(Integer idProducto);

    List<Calificacion> findByUsuario_IdUsuario(Integer idUsuario);

    Optional<Calificacion> findByUsuario_IdUsuarioAndProducto_IdProducto(Integer idUsuario, Integer idProducto);

    boolean existsByUsuario_IdUsuarioAndProducto_IdProducto(Integer idUsuario, Integer idProducto);

    List<Calificacion> findByProducto_IdProductoAndPuntuacionGreaterThanEqual(Integer idProducto, Integer puntuacion);

    List<Calificacion> findByProducto_IdProductoAndPuntuacionLessThanEqual(Integer idProducto, Integer puntuacion);

    @Transactional
    void deleteByProducto_IdProducto(Integer idProducto);

    @Transactional
    void deleteByUsuario_IdUsuario(Integer idUsuario);
}