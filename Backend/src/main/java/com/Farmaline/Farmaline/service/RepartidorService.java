package com.farmaline.farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.dto.RepartidorDTO;
import com.farmaline.farmaline.model.Administrador;
import com.farmaline.farmaline.model.Repartidor;
import com.farmaline.farmaline.repository.AdministradorRepository;
import com.farmaline.farmaline.repository.RepartidorRepository;

@Service
public class RepartidorService {

    private final RepartidorRepository repartidorRepository;
    private final AdministradorRepository administradorRepository;

    @Autowired
    public RepartidorService(RepartidorRepository repartidorRepository, AdministradorRepository administradorRepository) {
        this.repartidorRepository = repartidorRepository;
        this.administradorRepository = administradorRepository;
    }

    public List<RepartidorDTO> obtenerTodosRepartidores() {
        return repartidorRepository.findAll().stream()
                .map(this::convertirARepartidorDTO)
                .collect(Collectors.toList());
    }

    public Optional<RepartidorDTO> obtenerRepartidorPorId(Integer id) {
        return repartidorRepository.findById(id)
                .map(this::convertirARepartidorDTO);
    }

    @Transactional
    public RepartidorDTO crearRepartidor(RepartidorDTO repartidorDTO) {
        Repartidor repartidor = new Repartidor();
        repartidor.setNombre(repartidorDTO.getNombre());
        repartidor.setApellido(repartidorDTO.getApellido());
        repartidor.setCorreo_Electronico(repartidorDTO.getCorreo_Electronico()); // Asegúrate de que este campo exista en tu DTO y Model
        repartidor.setTelefono(repartidorDTO.getTelefono());
        repartidor.setContrasena(repartidorDTO.getContrasena());

        if (repartidorDTO.getIdAdministrador() != null) {
            Administrador administrador = administradorRepository.findById(repartidorDTO.getIdAdministrador())
                    .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));
            repartidor.setAdministrador(administrador);
        }

        repartidor = repartidorRepository.save(repartidor);
        return convertirARepartidorDTO(repartidor);
    }

    @Transactional
    public Optional<RepartidorDTO> actualizarRepartidor(Integer id, RepartidorDTO repartidorDTO) {
        return repartidorRepository.findById(id)
                .map(repartidorExistente -> {
                    repartidorExistente.setNombre(repartidorDTO.getNombre());
                    repartidorExistente.setApellido(repartidorDTO.getApellido());
                    repartidorExistente.setCorreo_Electronico(repartidorDTO.getCorreo_Electronico()); // Asegúrate de que este campo exista
                    repartidorExistente.setTelefono(repartidorDTO.getTelefono());
                    repartidorExistente.setContrasena(repartidorDTO.getContrasena());

                    if (repartidorDTO.getIdAdministrador() != null) {
                        Administrador administrador = administradorRepository.findById(repartidorDTO.getIdAdministrador())
                                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));
                        repartidorExistente.setAdministrador(administrador);
                    }
                    return convertirARepartidorDTO(repartidorRepository.save(repartidorExistente));
                });
    }

    public boolean eliminarRepartidor(Integer id) {
        if (repartidorRepository.existsById(id)) {
            repartidorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<RepartidorDTO> iniciarSesion(String telefono, String contrasena) {
        return repartidorRepository.findByTelefonoAndContrasena(telefono, contrasena)
                .map(this::convertirARepartidorDTO);
    }

    private RepartidorDTO convertirARepartidorDTO(Repartidor repartidor) {
        RepartidorDTO dto = new RepartidorDTO();
        dto.setIdRepartidor(repartidor.getIdRepartidor());
        dto.setNombre(repartidor.getNombre());
        dto.setApellido(repartidor.getApellido());
        dto.setCorreo_Electronico(repartidor.getCorreo_Electronico()); // Asegúrate de que este campo exista
        dto.setTelefono(repartidor.getTelefono());
        dto.setContrasena(repartidor.getContrasena());
        if (repartidor.getAdministrador() != null) {
            dto.setIdAdministrador(repartidor.getAdministrador().getIdAdministrador());
            dto.setNombreAdministrador(repartidor.getAdministrador().getNombre() + " " + repartidor.getAdministrador().getApellido());
        }
        return dto;
    }
}