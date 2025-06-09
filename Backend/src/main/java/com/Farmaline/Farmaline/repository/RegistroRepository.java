package com.Farmaline.Farmaline.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; 

import com.Farmaline.Farmaline.model.Doble_Verificacion;
import com.Farmaline.Farmaline.model.Pedido;
import com.Farmaline.Farmaline.model.Registro;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, Integer> {

    Optional<Registro> findByPedido(Pedido pedido);
    Optional<Registro> findByPedidoIdPedido(Integer pedidoId);

    Optional<Registro> findByDobleVerificacion(Doble_Verificacion dobleVerificacion);
    Optional<Registro> findByDobleVerificacionIdDobleVerifiacion(Integer dobleVerificacionId);

    Optional<Registro> findByPedidoIdPedidoAndDobleVerificacionIdDobleVerifiacion(Integer pedidoId, Integer dobleVerificacionId);
}