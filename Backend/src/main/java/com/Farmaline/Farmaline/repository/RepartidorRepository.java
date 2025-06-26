package com.farmaline.farmaline.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmaline.farmaline.model.Repartidor;

public interface RepartidorRepository extends JpaRepository<Repartidor, Integer> {
    Optional<Repartidor> findByTelefonoAndContrasena(String telefono, String contrasena);
}