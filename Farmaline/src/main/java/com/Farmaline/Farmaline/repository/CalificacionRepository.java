package com.Farmaline.Farmaline.repository;

import java.util.List;
import java.util.Optional; 

import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository;

import com.Farmaline.Farmaline.model.Calificacion;
import com.Farmaline.Farmaline.model.Producto;
import com.Farmaline.Farmaline.model.Usuario;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Integer> {

    List<Calificacion> findByProducto(Producto producto);

    List<Calificacion> findByProductoIdProducto(Integer productoId);

    List<Calificacion> findByUsuario(Usuario usuario);

    List<Calificacion> findByUsuarioIdUsuario(Integer usuarioId);

    List<Calificacion> findByPuntuacionGreaterThanEqual(Integer puntuacion);

    Optional<Calificacion> findByUsuarioIdUsuarioAndProductoIdProducto(Integer usuarioId, Integer productoId);

    long countByProductoIdProducto(Integer productoId);
}