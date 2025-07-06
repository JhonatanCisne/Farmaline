package com.farmaline.farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.dto.RepartidorDTO;
import com.farmaline.farmaline.model.Repartidor;
import com.farmaline.farmaline.repository.DobleVerificacionRepository;
import com.farmaline.farmaline.repository.PedidoRepository;
import com.farmaline.farmaline.repository.RepartidorRepository;

@Service
public class RepartidorService {

    @Autowired
    private RepartidorRepository repartidorRepository;

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private DobleVerificacionRepository dobleVerificacionRepository;

    public List<RepartidorDTO> getAllRepartidores() {
        return repartidorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<RepartidorDTO> getRepartidorById(Integer id) {
        return repartidorRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional
    public RepartidorDTO createRepartidor(RepartidorDTO repartidorDTO) {
        if (repartidorRepository.existsByCorreo_Electronico(repartidorDTO.getCorreo_Electronico())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado para otro repartidor.");
        }
        if (repartidorRepository.existsByTelefono(repartidorDTO.getTelefono())) {
            throw new IllegalArgumentException("El teléfono ya está registrado para otro repartidor.");
        }
        if (repartidorRepository.existsByPlaca(repartidorDTO.getPlaca())) {
            throw new IllegalArgumentException("La placa del vehículo ya está registrada para otro repartidor.");
        }

        Repartidor repartidor = convertToEntity(repartidorDTO);
        repartidor.setContrasena(repartidorDTO.getContrasena());
        Repartidor savedRepartidor = repartidorRepository.save(repartidor);
        return convertToDTO(savedRepartidor);
    }

    @Transactional
    public Optional<RepartidorDTO> updateRepartidor(Integer id, RepartidorDTO repartidorDTO) {
        return repartidorRepository.findById(id).map(existingRepartidor -> {
            if (!existingRepartidor.getCorreo_Electronico().equals(repartidorDTO.getCorreo_Electronico()) &&
                repartidorRepository.existsByCorreo_Electronico(repartidorDTO.getCorreo_Electronico())) {
                throw new IllegalArgumentException("El nuevo correo electrónico ya está en uso por otro repartidor.");
            }
            if (!existingRepartidor.getTelefono().equals(repartidorDTO.getTelefono()) &&
                repartidorRepository.existsByTelefono(repartidorDTO.getTelefono())) {
                throw new IllegalArgumentException("El nuevo teléfono ya está en uso por otro repartidor.");
            }
            if (!existingRepartidor.getPlaca().equals(repartidorDTO.getPlaca()) &&
                repartidorRepository.existsByPlaca(repartidorDTO.getPlaca())) {
                throw new IllegalArgumentException("La nueva placa del vehículo ya está en uso por otro repartidor.");
            }

            existingRepartidor.setNombre(repartidorDTO.getNombre());
            existingRepartidor.setApellido(repartidorDTO.getApellido());
            existingRepartidor.setCorreo_Electronico(repartidorDTO.getCorreo_Electronico()); // Asumo que en la Entidad es camelCase, DTO es snake_case
            existingRepartidor.setTelefono(repartidorDTO.getTelefono());
            existingRepartidor.setPlaca(repartidorDTO.getPlaca());

            if (repartidorDTO.getContrasena() != null && !repartidorDTO.getContrasena().isEmpty()) {
                existingRepartidor.setContrasena(repartidorDTO.getContrasena()); // Sin hashing temporalmente
            }

            Repartidor updatedRepartidor = repartidorRepository.save(existingRepartidor);
            return convertToDTO(updatedRepartidor);
        });
    }

    @Transactional
    public boolean deleteRepartidor(Integer id) {
        if (repartidorRepository.existsById(id)) {
            pedidoRepository.deleteByRepartidor_IdRepartidor(id);

            dobleVerificacionRepository.deleteByRepartidor_IdRepartidor(id);

            repartidorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<RepartidorDTO> authenticateRepartidor(String correoElectronico, String contrasena) {
        return repartidorRepository.findByCorreo_Electronico(correoElectronico) 
                .filter(repartidor -> contrasena.equals(repartidor.getContrasena())) 
                .map(this::convertToDTO);
    }

    private RepartidorDTO convertToDTO(Repartidor repartidor) {
        RepartidorDTO dto = new RepartidorDTO();
        dto.setIdRepartidor(repartidor.getIdRepartidor());
        dto.setNombre(repartidor.getNombre());
        dto.setApellido(repartidor.getApellido());
        dto.setCorreo_Electronico(repartidor.getCorreo_Electronico());
        dto.setTelefono(repartidor.getTelefono());
        dto.setPlaca(repartidor.getPlaca());
        return dto;
    }

    private Repartidor convertToEntity(RepartidorDTO repartidorDTO) {
        Repartidor repartidor = new Repartidor();
        repartidor.setIdRepartidor(repartidorDTO.getIdRepartidor());
        repartidor.setNombre(repartidorDTO.getNombre());
        repartidor.setApellido(repartidorDTO.getApellido());
        repartidor.setCorreo_Electronico(repartidorDTO.getCorreo_Electronico());
        repartidor.setTelefono(repartidorDTO.getTelefono());
        repartidor.setPlaca(repartidorDTO.getPlaca());
        repartidor.setContrasena(repartidorDTO.getContrasena());
        return repartidor;
    }
}