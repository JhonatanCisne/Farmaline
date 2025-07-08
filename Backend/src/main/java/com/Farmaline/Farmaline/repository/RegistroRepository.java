package com.farmaline.farmaline.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.model.Registro;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, Integer> {

    Optional<Registro> findByPedido_IdPedido(Integer idPedido);

    boolean existsByPedido_IdPedido(Integer idPedido);

    @Transactional
    void deleteByPedido_IdPedido(Integer idPedido);

}