package com.farmaline.farmaline.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; 

import com.farmaline.farmaline.model.Carrito;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {

    Optional<Carrito> findByUsuario_IdUsuario(Integer idUsuario);

    @Transactional 
    void deleteByUsuario_IdUsuario(Integer idUsuario);
}