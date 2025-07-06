package com.farmaline.farmaline.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.model.DetallePedido;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {

    List<DetallePedido> findByPedido_IdPedido(Integer idPedido);

    Optional<DetallePedido> findByPedido_IdPedidoAndProducto_IdProducto(Integer idPedido, Integer idProducto);

    @Transactional
    void deleteByPedido_IdPedido(Integer idPedido);

    List<DetallePedido> findByProducto_IdProducto(Integer idProducto);
    
    @Transactional
    void deleteByProducto_IdProducto(Integer idProducto);
}