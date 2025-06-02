package com.Farmaline.Farmaline.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository; // Importar si necesitas buscar por el objeto Usuario directamente
import org.springframework.stereotype.Repository;

import com.Farmaline.Farmaline.model.Carrito;
import com.Farmaline.Farmaline.model.Usuario;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {

    Optional<Carrito> findByUsuario(Usuario usuario);

    Optional<Carrito> findByUsuarioIdUsuario(Integer usuarioId);

    boolean existsByUsuarioIdUsuario(Integer usuarioId);
}