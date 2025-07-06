package com.farmaline.farmaline.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmaline.farmaline.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreoElectronico(String correoElectronico);

    Optional<Usuario> findByTelefono(String telefono);

    boolean existsByCorreoElectronico(String correoElectronico);

    boolean existsByTelefono(String telefono);
}