package com.farmaline.farmaline.service;

import com.farmaline.farmaline.dto.VehiculoDTO;
import com.farmaline.farmaline.model.Vehiculo;
import com.farmaline.farmaline.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    @Autowired
    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<VehiculoDTO> obtenerTodosVehiculos() {
        return vehiculoRepository.findAll().stream()
                .map(this::convertirAVehiculoDTO)
                .collect(Collectors.toList());
    }

    public Optional<VehiculoDTO> obtenerVehiculoPorId(Integer id) {
        return vehiculoRepository.findById(id)
                .map(this::convertirAVehiculoDTO);
    }

    public VehiculoDTO crearVehiculo(VehiculoDTO vehiculoDTO) {
        Vehiculo vehiculo = convertirAVehiculoEntidad(vehiculoDTO);
        vehiculo = vehiculoRepository.save(vehiculo);
        return convertirAVehiculoDTO(vehiculo);
    }

    public Optional<VehiculoDTO> actualizarVehiculo(Integer id, VehiculoDTO vehiculoDTO) {
        return vehiculoRepository.findById(id)
                .map(vehiculoExistente -> {
                    vehiculoExistente.setPlaca(vehiculoDTO.getPlaca());
                    vehiculoExistente.setCategoria(vehiculoDTO.getCategoria());
                    vehiculoExistente.setMarca(vehiculoDTO.getMarca());
                    vehiculoExistente.setModelo(vehiculoDTO.getModelo());
                    vehiculoExistente.setAnio(vehiculoDTO.getAnio());
                    return convertirAVehiculoDTO(vehiculoRepository.save(vehiculoExistente));
                });
    }

    public boolean eliminarVehiculo(Integer id) {
        if (vehiculoRepository.existsById(id)) {
            vehiculoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private VehiculoDTO convertirAVehiculoDTO(Vehiculo vehiculo) {
        VehiculoDTO dto = new VehiculoDTO();
        dto.setIdVehiculo(vehiculo.getIdVehiculo());
        dto.setPlaca(vehiculo.getPlaca());
        dto.setCategoria(vehiculo.getCategoria());
        dto.setMarca(vehiculo.getMarca());
        dto.setModelo(vehiculo.getModelo());
        dto.setAnio(vehiculo.getAnio());
        return dto;
    }

    private Vehiculo convertirAVehiculoEntidad(VehiculoDTO dto) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setIdVehiculo(dto.getIdVehiculo());
        vehiculo.setPlaca(dto.getPlaca());
        vehiculo.setCategoria(dto.getCategoria());
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setAnio(dto.getAnio());
        return vehiculo;
    }
}