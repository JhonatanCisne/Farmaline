package com.farmaline.farmaline.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmaline.farmaline.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
}