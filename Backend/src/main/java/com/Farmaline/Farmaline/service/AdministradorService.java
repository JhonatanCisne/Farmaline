package com.farmaline.farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.dto.AdministradorDTO;
import com.farmaline.farmaline.model.Administrador;
import com.farmaline.farmaline.repository.AdministradorRepository;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    public List<AdministradorDTO> getAllAdministradores() {
        return administradorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<AdministradorDTO> getAdministradorById(Integer id) {
        return administradorRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional
    public AdministradorDTO createAdministrador(AdministradorDTO administradorDTO) {
        if (administradorRepository.existsByUsuario(administradorDTO.getUsuario())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }

        Administrador administrador = convertToEntity(administradorDTO);
        administrador.setContrasena(administradorDTO.getContrasena());
        Administrador savedAdministrador = administradorRepository.save(administrador);
        return convertToDTO(savedAdministrador);
    }

    @Transactional
    public Optional<AdministradorDTO> updateAdministrador(Integer id, AdministradorDTO administradorDTO) {
        return administradorRepository.findById(id).map(existingAdministrador -> {
            if (!existingAdministrador.getUsuario().equals(administradorDTO.getUsuario()) &&
                administradorRepository.existsByUsuario(administradorDTO.getUsuario())) {
                throw new IllegalArgumentException("El nuevo nombre de usuario ya está en uso.");
            }

            existingAdministrador.setNombre(administradorDTO.getNombre());
            existingAdministrador.setApellido(administradorDTO.getApellido());
            existingAdministrador.setUsuario(administradorDTO.getUsuario());

            if (administradorDTO.getContrasena() != null && !administradorDTO.getContrasena().isEmpty()) {
                existingAdministrador.setContrasena(administradorDTO.getContrasena());
            }

            Administrador updatedAdministrador = administradorRepository.save(existingAdministrador);
            return convertToDTO(updatedAdministrador);
        });
    }

    @Transactional
    public boolean deleteAdministrador(Integer id) {
        if (administradorRepository.existsById(id)) {
            administradorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<AdministradorDTO> authenticateAdministrador(String usuario, String contrasena) {
        return administradorRepository.findByUsuario(usuario)
                .filter(administrador -> contrasena.equals(administrador.getContrasena())) 
                .map(this::convertToDTO);
    }


    private AdministradorDTO convertToDTO(Administrador administrador) {
        AdministradorDTO dto = new AdministradorDTO();
        dto.setIdAdministrador(administrador.getIdAdministrador());
        dto.setNombre(administrador.getNombre());
        dto.setApellido(administrador.getApellido());
        dto.setUsuario(administrador.getUsuario());
        return dto;
    }

    private Administrador convertToEntity(AdministradorDTO administradorDTO) {
        Administrador administrador = new Administrador();
        administrador.setIdAdministrador(administradorDTO.getIdAdministrador()); 
        administrador.setNombre(administradorDTO.getNombre());
        administrador.setApellido(administradorDTO.getApellido());
        administrador.setUsuario(administradorDTO.getUsuario());
        administrador.setContrasena(administradorDTO.getContrasena());
        return administrador;
    }
}