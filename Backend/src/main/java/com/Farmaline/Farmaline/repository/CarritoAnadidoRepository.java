package com.farmaline.farmaline.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.model.Carrito_Anadido;

@Repository
public interface CarritoAnadidoRepository extends JpaRepository<Carrito_Anadido, Integer> {

    List<Carrito_Anadido> findByCarrito_IdCarrito(Integer idCarrito);

    Optional<Carrito_Anadido> findByCarrito_IdCarritoAndProducto_IdProducto(Integer idCarrito, Integer idProducto);

    @Transactional
    void deleteByCarrito_IdCarrito(Integer idCarrito);

    @Transactional
    void deleteByCarrito_IdCarritoAndProducto_IdProducto(Integer idCarrito, Integer idProducto);

    boolean existsByCarrito_IdCarritoAndProducto_IdProducto(Integer idCarrito, Integer idProducto);

    @Transactional
    void deleteByProducto_IdProducto(Integer idProducto);
}