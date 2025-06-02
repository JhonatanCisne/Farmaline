package com.Farmaline.Farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Farmaline.Farmaline.dto.VehiculoDTO;
import com.Farmaline.Farmaline.model.Vehiculo;
import com.Farmaline.Farmaline.repository.VehiculoRepository;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    @Autowired
    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    private VehiculoDTO convertToDto(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return null;
        }
        VehiculoDTO dto = new VehiculoDTO();
        dto.setIdVehiculo(vehiculo.getIdVehiculo());
        dto.setPlaca(vehiculo.getPlaca());
        dto.setCategoria(vehiculo.getCategoria());
        dto.setMarca(vehiculo.getMarca());
        dto.setModelo(vehiculo.getModelo());
        dto.setAnio(vehiculo.getAnio());
        return dto;
    }

    private Vehiculo convertToEntity(VehiculoDTO dto) {
        if (dto == null) {
            return null;
        }
        Vehiculo vehiculo = new Vehiculo();
        if (dto.getIdVehiculo() != null) {
            vehiculo.setIdVehiculo(dto.getIdVehiculo());
        }
        vehiculo.setPlaca(dto.getPlaca());
        vehiculo.setCategoria(dto.getCategoria());
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setAnio(dto.getAnio());
        return vehiculo;
    }


    @Transactional(readOnly = true)
    public List<VehiculoDTO> findAllVehiculos() {
        return vehiculoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<VehiculoDTO> findVehiculoById(Integer id) {
        return vehiculoRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Optional<VehiculoDTO> findVehiculoByPlaca(String placa) {
        return vehiculoRepository.findByPlaca(placa)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<VehiculoDTO> findVehiculosByMarca(String marca) {
        return vehiculoRepository.findByMarcaContainingIgnoreCase(marca).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<VehiculoDTO> findVehiculosByAnioGreaterThanEqual(Integer anio) {
        return vehiculoRepository.findByAnioGreaterThanEqual(anio).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public VehiculoDTO createVehiculo(VehiculoDTO vehiculoDTO) {
        if (vehiculoDTO.getPlaca() == null || vehiculoDTO.getPlaca().trim().isEmpty()) {
            throw new IllegalArgumentException("La placa del vehículo es obligatoria.");
        }
        if (vehiculoRepository.findByPlaca(vehiculoDTO.getPlaca()).isPresent()) {
            throw new IllegalStateException("Ya existe un vehículo con esta placa: " + vehiculoDTO.getPlaca());
        }
        if (vehiculoDTO.getAnio() == null || vehiculoDTO.getAnio() <= 1900) { 
            throw new IllegalArgumentException("El año del vehículo debe ser válido.");
        }

        Vehiculo vehiculo = convertToEntity(vehiculoDTO);
        Vehiculo savedVehiculo = vehiculoRepository.save(vehiculo);
        return convertToDto(savedVehiculo);
    }

    @Transactional
    public VehiculoDTO updateVehiculo(Integer id, VehiculoDTO vehiculoDTO) {
        return vehiculoRepository.findById(id)
            .map(vehiculo -> {
                if (vehiculoDTO.getPlaca() != null) {
                    if (!vehiculo.getPlaca().equals(vehiculoDTO.getPlaca()) &&
                        vehiculoRepository.findByPlaca(vehiculoDTO.getPlaca()).isPresent()) {
                        throw new IllegalStateException("La nueva placa ya está en uso.");
                    }
                    vehiculo.setPlaca(vehiculoDTO.getPlaca());
                }
                if (vehiculoDTO.getCategoria() != null) {
                    vehiculo.setCategoria(vehiculoDTO.getCategoria());
                }
                if (vehiculoDTO.getMarca() != null) {
                    vehiculo.setMarca(vehiculoDTO.getMarca());
                }
                if (vehiculoDTO.getModelo() != null) {
                    vehiculo.setModelo(vehiculoDTO.getModelo());
                }
                if (vehiculoDTO.getAnio() != null && vehiculoDTO.getAnio() > 1900) {
                    vehiculo.setAnio(vehiculoDTO.getAnio());
                }
                
                Vehiculo updatedVehiculo = vehiculoRepository.save(vehiculo);
                return convertToDto(updatedVehiculo);
            }).orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + id));
    }

    @Transactional
    public void deleteVehiculo(Integer id) {
        if (!vehiculoRepository.existsById(id)) {
            throw new RuntimeException("Vehículo con ID " + id + " no encontrado para eliminar.");
        }
        vehiculoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<VehiculoDTO> findByModeloContainingIgnoreCase(String modelo) {
        return vehiculoRepository.findByModeloContainingIgnoreCase(modelo).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}