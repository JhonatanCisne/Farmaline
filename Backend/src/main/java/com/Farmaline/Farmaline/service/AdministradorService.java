package com.farmaline.farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmaline.farmaline.dto.AdministradorDTO;
import com.farmaline.farmaline.model.Administrador;
import com.farmaline.farmaline.repository.AdministradorRepository;

@Service
public class AdministradorService {

    private final AdministradorRepository administradorRepository;

    @Autowired
    public AdministradorService(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    public List<AdministradorDTO> obtenerTodosAdministradores() {
        return administradorRepository.findAll().stream()
                .map(this::convertirAAdminDTO)
                .collect(Collectors.toList());
    }

    public Optional<AdministradorDTO> obtenerAdministradorPorId(Integer id) {
        return administradorRepository.findById(id)
                .map(this::convertirAAdminDTO);
    }

    public AdministradorDTO crearAdministrador(AdministradorDTO administradorDTO) {
        Administrador administrador = convertirAAdminEntidad(administradorDTO);
        administrador = administradorRepository.save(administrador);
        return convertirAAdminDTO(administrador);
    }

    public Optional<AdministradorDTO> actualizarAdministrador(Integer id, AdministradorDTO administradorDTO) {
        return administradorRepository.findById(id)
                .map(administradorExistente -> {
                    administradorExistente.setNombre(administradorDTO.getNombre());
                    administradorExistente.setApellido(administradorDTO.getApellido());
                    administradorExistente.setContrasena(administradorDTO.getContrasena());
                    return convertirAAdminDTO(administradorRepository.save(administradorExistente));
                });
    }

    public boolean eliminarAdministrador(Integer id) {
        if (administradorRepository.existsById(id)) {
            administradorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<AdministradorDTO> iniciarSesion(String nombre, String contrasena) {
        return administradorRepository.findByNombreAndContrasena(nombre, contrasena)
                .map(this::convertirAAdminDTO);
    }

    private AdministradorDTO convertirAAdminDTO(Administrador administrador) {
        AdministradorDTO dto = new AdministradorDTO();
        dto.setIdAdministrador(administrador.getIdAdministrador());
        dto.setNombre(administrador.getNombre());
        dto.setApellido(administrador.getApellido());
        dto.setContrasena(administrador.getContrasena());
        return dto;
    }

    private Administrador convertirAAdminEntidad(AdministradorDTO dto) {
        Administrador administrador = new Administrador();
        administrador.setIdAdministrador(dto.getIdAdministrador());
        administrador.setNombre(dto.getNombre());
        administrador.setApellido(dto.getApellido());
        administrador.setContrasena(dto.getContrasena());
        return administrador;
    }
}