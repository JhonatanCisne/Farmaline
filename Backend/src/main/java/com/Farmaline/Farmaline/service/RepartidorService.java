package com.Farmaline.Farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Farmaline.Farmaline.dto.RepartidorDTO;
import com.Farmaline.Farmaline.model.Administrador;
import com.Farmaline.Farmaline.model.Repartidor;
import com.Farmaline.Farmaline.model.Vehiculo;
import com.Farmaline.Farmaline.repository.AdministradorRepository;
import com.Farmaline.Farmaline.repository.RepartidorRepository;
import com.Farmaline.Farmaline.repository.VehiculoRepository;

@Service
public class RepartidorService {

    private final RepartidorRepository repartidorRepository;
    private final VehiculoRepository vehiculoRepository;
    private final AdministradorRepository administradorRepository;

    @Autowired
    public RepartidorService(
            RepartidorRepository repartidorRepository,
            VehiculoRepository vehiculoRepository,
            AdministradorRepository administradorRepository) {
        this.repartidorRepository = repartidorRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.administradorRepository = administradorRepository;
    }

    private RepartidorDTO convertToDto(Repartidor repartidor) {
        if (repartidor == null) {
            return null;
        }
        RepartidorDTO dto = new RepartidorDTO();
        dto.setIdRepartidor(repartidor.getIdRepartidor());
        dto.setNombre(repartidor.getNombre());
        dto.setApellido(repartidor.getApellido());
        dto.setTelefono(repartidor.getTelefono());
        if (repartidor.getVehiculo() != null) {
            dto.setIdVehiculo(repartidor.getVehiculo().getIdVehiculo());
        }
        if (repartidor.getAdministrador() != null) {
            dto.setIdAdministrador(repartidor.getAdministrador().getIdAdministrador());
        }
        return dto;
    }

    private Repartidor convertToEntity(RepartidorDTO dto) {
        if (dto == null) {
            return null;
        }
        Repartidor repartidor = new Repartidor();

        repartidor.setNombre(dto.getNombre());
        repartidor.setApellido(dto.getApellido());
        repartidor.setTelefono(dto.getTelefono());
        
        if (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) {
            repartidor.setContrasena(dto.getContrasena());
        } else {
            throw new IllegalArgumentException("La contraseña es obligatoria para el repartidor.");
        }

        if (dto.getIdVehiculo() != null) {
            Vehiculo vehiculo = vehiculoRepository.findById(dto.getIdVehiculo())
                    .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + dto.getIdVehiculo()));
            repartidor.setVehiculo(vehiculo);
        } else {
            throw new IllegalArgumentException("ID de vehículo es obligatorio para el repartidor.");
        }

        if (dto.getIdAdministrador() != null) {
            Administrador administrador = administradorRepository.findById(dto.getIdAdministrador())
                    .orElseThrow(() -> new RuntimeException("Administrador no encontrado con ID: " + dto.getIdAdministrador()));
            repartidor.setAdministrador(administrador);
        } else {
            throw new IllegalArgumentException("ID de administrador es obligatorio para el repartidor.");
        }
        return repartidor;
    }

    @Transactional(readOnly = true)
    public List<RepartidorDTO> findAllRepartidores() {
        return repartidorRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<RepartidorDTO> findRepartidorById(Integer id) {
        return repartidorRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Optional<RepartidorDTO> findRepartidorByTelefono(String telefono) {
        return repartidorRepository.findByTelefono(telefono)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<RepartidorDTO> findRepartidoresByAdministradorId(Integer adminId) {
        return repartidorRepository.findByAdministradorIdAdministrador(adminId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RepartidorDTO createRepartidor(RepartidorDTO repartidorDTO) {
        if (repartidorDTO.getTelefono() == null || repartidorDTO.getTelefono().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de teléfono es obligatorio.");
        }
        if (repartidorRepository.findByTelefono(repartidorDTO.getTelefono()).isPresent()) {
            throw new IllegalStateException("Ya existe un repartidor con este número de teléfono.");
        }
        if (repartidorDTO.getContrasena() == null || repartidorDTO.getContrasena().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria para el repartidor.");
        }

        Repartidor repartidor = convertToEntity(repartidorDTO);
        Repartidor savedRepartidor = repartidorRepository.save(repartidor);
        return convertToDto(savedRepartidor);
    }

    @Transactional
    public RepartidorDTO updateRepartidor(Integer id, RepartidorDTO repartidorDTO) {
        return repartidorRepository.findById(id)
            .map(repartidor -> {
                if (repartidorDTO.getNombre() != null) {
                    repartidor.setNombre(repartidorDTO.getNombre());
                }
                if (repartidorDTO.getApellido() != null) {
                    repartidor.setApellido(repartidorDTO.getApellido());
                }
                if (repartidorDTO.getTelefono() != null) {
                    if (!repartidor.getTelefono().equals(repartidorDTO.getTelefono()) &&
                        repartidorRepository.findByTelefono(repartidorDTO.getTelefono()).isPresent()) {
                        throw new IllegalStateException("El nuevo número de teléfono ya está en uso.");
                    }
                    repartidor.setTelefono(repartidorDTO.getTelefono());
                }
                if (repartidorDTO.getContrasena() != null && !repartidorDTO.getContrasena().isEmpty()) {
                    repartidor.setContrasena(repartidorDTO.getContrasena());
                }
                
                Repartidor updatedRepartidor = repartidorRepository.save(repartidor);
                return convertToDto(updatedRepartidor);
            }).orElseThrow(() -> new RuntimeException("Repartidor no encontrado con ID: " + id));
    }

    @Transactional
    public void deleteRepartidor(Integer id) {
        if (!repartidorRepository.existsById(id)) {
            throw new RuntimeException("Repartidor con ID " + id + " no encontrado para eliminar.");
        }
        repartidorRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public boolean authenticateRepartidor(String telefono, String rawPassword) {
        Optional<Repartidor> repartidorOptional = repartidorRepository.findByTelefono(telefono);
        if (repartidorOptional.isPresent()) {
            Repartidor repartidor = repartidorOptional.get();
            return rawPassword.equals(repartidor.getContrasena());
        }
        return false;
    }
}