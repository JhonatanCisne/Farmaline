package com.Farmaline.Farmaline.repository;

import java.util.List;
import java.util.Optional;  

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;    

import com.Farmaline.Farmaline.model.Doble_Verificacion;
import com.Farmaline.Farmaline.model.Pedido;
import com.Farmaline.Farmaline.model.Repartidor;
import com.Farmaline.Farmaline.model.Usuario;

@Repository
public interface Doble_VerificacionRepository extends JpaRepository<Doble_Verificacion, Integer> {

    Optional<Doble_Verificacion> findByPedido(Pedido pedido);
    Optional<Doble_Verificacion> findByPedidoIdPedido(Integer pedidoId);

    List<Doble_Verificacion> findByUsuario(Usuario usuario);
    List<Doble_Verificacion> findByUsuarioIdUsuario(Integer usuarioId);

    List<Doble_Verificacion> findByRepartidor(Repartidor repartidor);
    List<Doble_Verificacion> findByRepartidorIdRepartidor(Integer repartidorId);

    List<Doble_Verificacion> findByEstadoUsuario(String estadoUsuario);
    List<Doble_Verificacion> findByEstadoRepartidor(String estadoRepartidor);

    List<Doble_Verificacion> findByEstadoUsuarioAndEstadoRepartidor(String estadoUsuario, String estadoRepartidor);

    Optional<Doble_Verificacion> findByUsuarioIdUsuarioAndRepartidorIdRepartidorAndPedidoIdPedido(
            Integer usuarioId, Integer repartidorId, Integer pedidoId);
}