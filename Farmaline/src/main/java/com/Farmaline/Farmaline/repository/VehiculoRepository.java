package com.Farmaline.Farmaline.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Farmaline.Farmaline.model.Vehiculo;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {

    Optional<Vehiculo> findByPlaca(String placa);

    boolean existsByPlaca(String placa);

    List<Vehiculo> findByCategoria(String categoria);

    List<Vehiculo> findByMarcaContainingIgnoreCase(String marca);

    List<Vehiculo> findByModeloContainingIgnoreCase(String modelo);

    List<Vehiculo> findByAnio(Integer anio);

    List<Vehiculo> findByAnioGreaterThanEqual(Integer anio);

    List<Vehiculo> findByMarcaAndModelo(String marca, String modelo);
}