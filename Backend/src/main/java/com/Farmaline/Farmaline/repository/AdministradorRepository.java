package com.Farmaline.Farmaline.repository;

import java.util.List;
import java.util.Optional; 

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Farmaline.Farmaline.model.Administrador;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {

    Optional<Administrador> findByNombre(String nombre);

    Optional<Administrador> findByNombreAndApellido(String nombre, String apellido);

    List<Administrador> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombrePart, String apellidoPart);

    boolean existsByNombreAndApellido(String nombre, String apellido);
}