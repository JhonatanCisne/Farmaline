package com.farmaline.farmaline.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByUsuario_IdUsuario(Integer idUsuario);

    List<Pedido> findByRepartidor_IdRepartidor(Integer idRepartidor);

    List<Pedido> findByFechaBetween(LocalDate startDate, LocalDate endDate);

    List<Pedido> findByFechaAndUsuario_IdUsuario(LocalDate fecha, Integer idUsuario);

    @Transactional
    void deleteByUsuario_IdUsuario(Integer idUsuario);

    @Transactional
    void deleteByRepartidor_IdRepartidor(Integer idRepartidor);
}