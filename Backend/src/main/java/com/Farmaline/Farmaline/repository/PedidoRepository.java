package com.Farmaline.Farmaline.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;  

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Farmaline.Farmaline.model.Carrito;
import com.Farmaline.Farmaline.model.Pedido;
import com.Farmaline.Farmaline.model.Repartidor;
import com.Farmaline.Farmaline.model.Usuario;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByUsuario(Usuario usuario);

    List<Pedido> findByUsuarioIdUsuario(Integer usuarioId);

    List<Pedido> findByRepartidor(Repartidor repartidor);

    List<Pedido> findByRepartidorIdRepartidor(Integer repartidorId);

    List<Pedido> findByFecha(LocalDate fecha);

    List<Pedido> findByFechaBetween(LocalDate startDate, LocalDate endDate);

    Optional<Pedido> findByCarrito(Carrito carrito); 
    Optional<Pedido> findByCarritoIdCarrito(Integer carritoId);
}