package com.Farmaline.Farmaline.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Farmaline.Farmaline.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreoElectronico(String correoElectronico);

    boolean existsByCorreoElectronico(String correoElectronico);

    Optional<Usuario> findByTelefono(String telefono);

    boolean existsByTelefono(String telefono);

    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    List<Usuario> findByApellidoContainingIgnoreCase(String apellido);

    List<Usuario> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombrePart, String apellidoPart);

    List<Usuario> findByDomicilioContainingIgnoreCase(String domicilioPart);
}