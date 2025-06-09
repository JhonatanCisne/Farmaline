package com.Farmaline.Farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Farmaline.Farmaline.dto.AdministradorDTO;
import com.Farmaline.Farmaline.dto.AdministradorLoginDTO;
import com.Farmaline.Farmaline.model.Administrador;
import com.Farmaline.Farmaline.repository.AdministradorRepository;

@Service
public class AdministradorService {

    private final AdministradorRepository administradorRepository;

    @Autowired
    public AdministradorService(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    private AdministradorDTO convertToDto(Administrador administrador) {
        if (administrador == null) {
            return null;
        }
        AdministradorDTO dto = new AdministradorDTO();
        dto.setNombre(administrador.getNombre());
        dto.setApellido(administrador.getApellido());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<AdministradorDTO> findAllAdministradores() {
        return administradorRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<AdministradorDTO> findAdministradorById(Integer id) {
        return administradorRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Optional<AdministradorDTO> findAdministradorByNombre(String nombre) {
        return administradorRepository.findByNombre(nombre)
                .map(this::convertToDto);
    }

    @Transactional
    public AdministradorDTO createAdministrador(AdministradorDTO administradorDTO, String contrasena) {
        // Validaciones de negocio
        if (administradorDTO.getNombre() == null || administradorDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del administrador es obligatorio.");
        }
        if (administradorRepository.findByNombre(administradorDTO.getNombre()).isPresent()) {
            throw new IllegalStateException("Ya existe un administrador con el nombre: " + administradorDTO.getNombre());
        }
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria para el administrador.");
        }

        Administrador administrador = new Administrador();
        administrador.setNombre(administradorDTO.getNombre());
        administrador.setApellido(administradorDTO.getApellido());
        administrador.setContrasena(contrasena); 

        Administrador savedAdministrador = administradorRepository.save(administrador);
        return convertToDto(savedAdministrador);
    }

    @Transactional
    public AdministradorDTO updateAdministrador(Integer id, AdministradorDTO administradorDTO) {
        return administradorRepository.findById(id)
            .map(administrador -> {
                if (administradorDTO.getNombre() != null) {
                    if (!administrador.getNombre().equals(administradorDTO.getNombre()) &&
                        administradorRepository.findByNombre(administradorDTO.getNombre()).isPresent()) {
                        throw new IllegalStateException("El nuevo nombre de administrador ya está en uso.");
                    }
                    administrador.setNombre(administradorDTO.getNombre());
                }
                if (administradorDTO.getApellido() != null) {
                    administrador.setApellido(administradorDTO.getApellido());
                }
                
                Administrador updatedAdministrador = administradorRepository.save(administrador);
                return convertToDto(updatedAdministrador);
            }).orElseThrow(() -> new RuntimeException("Administrador no encontrado con ID: " + id));
    }

    @Transactional
    public AdministradorDTO updateAdministradorPassword(Integer id, String newPassword) {
        return administradorRepository.findById(id)
            .map(administrador -> {
                if (newPassword == null || newPassword.trim().isEmpty()) {
                    throw new IllegalArgumentException("La nueva contraseña no puede estar vacía.");
                }
                administrador.setContrasena(newPassword); 
                Administrador updatedAdministrador = administradorRepository.save(administrador);
                return convertToDto(updatedAdministrador);
            }).orElseThrow(() -> new RuntimeException("Administrador no encontrado con ID: " + id));
    }

    @Transactional
    public void deleteAdministrador(Integer id) {
        if (!administradorRepository.existsById(id)) {
            throw new RuntimeException("Administrador con ID " + id + " no encontrado para eliminar.");
        }
        administradorRepository.deleteById(id);
    }
    

    @Transactional(readOnly = true)
    public Optional<AdministradorDTO> authenticateAdministrador(AdministradorLoginDTO loginDTO) {
        Optional<Administrador> administradorOptional = administradorRepository.findByNombre(loginDTO.getNombre());
        
        if (administradorOptional.isPresent()) {
            Administrador administrador = administradorOptional.get();
            if (loginDTO.getContrasena().equals(administrador.getContrasena())) {
                return Optional.of(convertToDto(administrador));
            }
        }
        return Optional.empty(); 
    }
}