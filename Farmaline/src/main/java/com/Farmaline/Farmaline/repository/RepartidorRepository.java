package com.Farmaline.Farmaline.repository;

import java.util.List;
import java.util.Optional;
   
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Farmaline.Farmaline.model.Administrador;
import com.Farmaline.Farmaline.model.Repartidor;
import com.Farmaline.Farmaline.model.Vehiculo;

@Repository
public interface RepartidorRepository extends JpaRepository<Repartidor, Integer> {

    Optional<Repartidor> findByTelefono(String telefono);

    boolean existsByTelefono(String telefono);

    List<Repartidor> findByNombreContainingIgnoreCase(String nombre);

    List<Repartidor> findByApellidoContainingIgnoreCase(String apellido);

    List<Repartidor> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombrePart, String apellidoPart);

    Optional<Repartidor> findByVehiculo(Vehiculo vehiculo);

    Optional<Repartidor> findByVehiculoIdVehiculo(Integer vehiculoId);

    List<Repartidor> findByAdministrador(Administrador administrador);

    List<Repartidor> findByAdministradorIdAdministrador(Integer administradorId);
}