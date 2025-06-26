package com.farmaline.farmaline.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmaline.farmaline.model.DetallePedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {
    List<DetallePedido> findByPedidoIdPedido(Integer idPedido);
}