package com.farmaline.farmaline.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmaline.farmaline.model.Administrador;

public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {
    Optional<Administrador> findByNombreAndContrasena(String nombre, String contrasena);
}