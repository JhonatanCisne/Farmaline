package com.farmaline.farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Asegúrate de tener esta importación

import com.farmaline.farmaline.dto.RepartidorDTO;
import com.farmaline.farmaline.model.Repartidor;
import com.farmaline.farmaline.repository.PedidoRepository;
import com.farmaline.farmaline.repository.RepartidorRepository;

@Service
public class RepartidorService {

    @Autowired
    private RepartidorRepository repartidorRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired(required = false)
    private BCryptPasswordEncoder passwordEncoder;

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
        if (repartidorRepository.existsByCorreoElectronico(repartidorDTO.getCorreoElectronico())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado para otro repartidor.");
        }
        if (repartidorRepository.existsByTelefono(repartidorDTO.getTelefono())) {
            throw new IllegalArgumentException("El teléfono ya está registrado para otro repartidor.");
        }
        if (repartidorRepository.existsByPlaca(repartidorDTO.getPlaca())) {
            throw new IllegalArgumentException("La placa del vehículo ya está registrada para otro repartidor.");
        }

        Repartidor repartidor = convertToEntity(repartidorDTO);
        if (repartidorDTO.getContrasena() != null && passwordEncoder != null) {
            repartidor.setContrasena(passwordEncoder.encode(repartidorDTO.getContrasena()));
        } else {
            repartidor.setContrasena(repartidorDTO.getContrasena()); // Esto solo si no hay encoder, no recomendado
        }
        Repartidor savedRepartidor = repartidorRepository.save(repartidor);
        return convertToDTO(savedRepartidor);
    }

    @Transactional
    public Optional<RepartidorDTO> updateRepartidor(Integer id, RepartidorDTO repartidorDTO) {
        return repartidorRepository.findById(id).map(existingRepartidor -> {
            if (!existingRepartidor.getCorreoElectronico().equals(repartidorDTO.getCorreoElectronico()) &&
                repartidorRepository.existsByCorreoElectronico(repartidorDTO.getCorreoElectronico())) {
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
            existingRepartidor.setCorreoElectronico(repartidorDTO.getCorreoElectronico());
            existingRepartidor.setTelefono(repartidorDTO.getTelefono());
            existingRepartidor.setPlaca(repartidorDTO.getPlaca());

            // No se actualiza la contraseña aquí, ya que hay un método dedicado para ello.
            // Si el DTO de actualización contiene una contraseña, esta línea se encargaría:
            // if (repartidorDTO.getContrasena() != null && !repartidorDTO.getContrasena().isEmpty()) {
            //     if (passwordEncoder != null) {
            //         existingRepartidor.setContrasena(passwordEncoder.encode(repartidorDTO.getContrasena()));
            //     } else {
            //         existingRepartidor.setContrasena(repartidorDTO.getContrasena());
            //     }
            // }

            Repartidor updatedRepartidor = repartidorRepository.save(existingRepartidor);
            return convertToDTO(updatedRepartidor);
        });
    }

    @Transactional
    public boolean deleteRepartidor(Integer id) {
        if (repartidorRepository.existsById(id)) {
            pedidoRepository.deleteByRepartidor_IdRepartidor(id);

            repartidorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updateRepartidorPassword(Integer id, String newPassword) {
        return repartidorRepository.findById(id).map(repartidor -> {
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                if (passwordEncoder != null) {
                    repartidor.setContrasena(passwordEncoder.encode(newPassword));
                } else {
                    repartidor.setContrasena(newPassword); // Esto solo si no hay encoder, no recomendado
                }
                repartidorRepository.save(repartidor);
                return true;
            }
            return false;
        }).orElse(false);
    }

    public Optional<RepartidorDTO> authenticateRepartidor(String correoElectronico, String contrasena) {
        return repartidorRepository.findByCorreoElectronico(correoElectronico)
                .filter(repartidor -> passwordEncoder != null ?
                                        passwordEncoder.matches(contrasena, repartidor.getContrasena()) :
                                        contrasena.equals(repartidor.getContrasena()))
                .map(this::convertToDTO);
    }

    private RepartidorDTO convertToDTO(Repartidor repartidor) {
        RepartidorDTO dto = new RepartidorDTO();
        dto.setIdRepartidor(repartidor.getIdRepartidor());
        dto.setNombre(repartidor.getNombre());
        dto.setApellido(repartidor.getApellido());
        dto.setCorreoElectronico(repartidor.getCorreoElectronico());
        dto.setTelefono(repartidor.getTelefono());
        dto.setPlaca(repartidor.getPlaca());
        return dto;
    }

    private Repartidor convertToEntity(RepartidorDTO repartidorDTO) {
        Repartidor repartidor = new Repartidor();
        repartidor.setIdRepartidor(repartidorDTO.getIdRepartidor());
        repartidor.setNombre(repartidorDTO.getNombre());
        repartidor.setApellido(repartidorDTO.getApellido());
        repartidor.setCorreoElectronico(repartidorDTO.getCorreoElectronico());
        repartidor.setTelefono(repartidorDTO.getTelefono());
        repartidor.setPlaca(repartidorDTO.getPlaca());
        repartidor.setContrasena(repartidorDTO.getContrasena());
        return repartidor;
    }
}