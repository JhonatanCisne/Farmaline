package com.farmaline.farmaline.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmaline.farmaline.model.Administrador;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {

    Optional<Administrador> findByUsuario(String usuario);

    boolean existsByUsuario(String usuario);
}