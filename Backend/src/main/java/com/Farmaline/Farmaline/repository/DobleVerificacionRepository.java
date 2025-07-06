package com.farmaline.farmaline.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.model.Doble_Verificacion;

@Repository
public interface DobleVerificacionRepository extends JpaRepository<Doble_Verificacion, Integer> {

    Optional<Doble_Verificacion> findByPedido_IdPedido(Integer idPedido);

    List<Doble_Verificacion> findByUsuario_IdUsuario(Integer idUsuario);

    List<Doble_Verificacion> findByRepartidor_IdRepartidor(Integer idRepartidor);

    boolean existsByPedido_IdPedido(Integer idPedido);

    @Transactional
    void deleteByPedido_IdPedido(Integer idPedido);

    @Transactional
    void deleteByRepartidor_IdRepartidor(Integer idRepartidor);

    @Transactional
    void deleteByUsuario_IdUsuario(Integer idUsuario);
}