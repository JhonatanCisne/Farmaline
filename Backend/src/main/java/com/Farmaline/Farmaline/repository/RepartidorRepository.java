package com.farmaline.farmaline.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.farmaline.farmaline.model.Repartidor;

@Repository
public interface RepartidorRepository extends JpaRepository<Repartidor, Integer> {

    Optional<Repartidor> findByCorreoElectronico(String correoElectronico);

    Optional<Repartidor> findByTelefono(String telefono);

    Optional<Repartidor> findByPlaca(String placa);

    boolean existsByCorreoElectronico(String correoElectronico);

    boolean existsByTelefono(String telefono);

    boolean existsByPlaca(String placa);

}