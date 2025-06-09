package com.Farmaline.Farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Farmaline.Farmaline.dto.UsuarioDTO;
import com.Farmaline.Farmaline.dto.UsuarioLoginDTO;
import com.Farmaline.Farmaline.dto.UsuarioRegistroDTO;
import com.Farmaline.Farmaline.model.Usuario;
import com.Farmaline.Farmaline.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioDTO registrarUsuario(UsuarioRegistroDTO usuarioRegistroDTO) {
        if (usuarioRepository.existsByCorreoElectronico(usuarioRegistroDTO.getCorreoElectronico())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }
        if (usuarioRepository.existsByTelefono(usuarioRegistroDTO.getTelefono())) {
            throw new IllegalArgumentException("El número de teléfono ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioRegistroDTO.getNombre());
        usuario.setApellido(usuarioRegistroDTO.getApellido());
        usuario.setCorreoElectronico(usuarioRegistroDTO.getCorreoElectronico());
        usuario.setDomicilio(usuarioRegistroDTO.getDomicilio());
        usuario.setTelefono(usuarioRegistroDTO.getTelefono());
        usuario.setContrasena(usuarioRegistroDTO.getContrasena()); 

        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        return convertirAUsuarioDTO(nuevoUsuario);
    }

    public Optional<UsuarioDTO> loginUsuario(UsuarioLoginDTO usuarioLoginDTO) {
        return usuarioRepository.findByCorreoElectronico(usuarioLoginDTO.getCorreoElectronico())
                .filter(usuario -> usuario.getContrasena().equals(usuarioLoginDTO.getContrasena())) 
                .map(this::convertirAUsuarioDTO);
    }

    public Optional<UsuarioDTO> obtenerUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id)
                .map(this::convertirAUsuarioDTO);
    }

    public UsuarioDTO actualizarUsuario(Integer id, UsuarioDTO usuarioDTO) {
        return usuarioRepository.findById(id).map(usuarioExistente -> {
            usuarioExistente.setNombre(usuarioDTO.getNombre());
            usuarioExistente.setApellido(usuarioDTO.getApellido());
            usuarioExistente.setCorreoElectronico(usuarioDTO.getCorreoElectronico());
            usuarioExistente.setDomicilio(usuarioDTO.getDomicilio());
            usuarioExistente.setTelefono(usuarioDTO.getTelefono());
            Usuario usuarioActualizado = usuarioRepository.save(usuarioExistente);
            return convertirAUsuarioDTO(usuarioActualizado);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    public void eliminarUsuario(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    public List<UsuarioDTO> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
    }

    public List<UsuarioDTO> buscarUsuariosPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
    }

    public List<UsuarioDTO> buscarUsuariosPorApellido(String apellido) {
        return usuarioRepository.findByApellidoContainingIgnoreCase(apellido).stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
    }

    public List<UsuarioDTO> buscarUsuariosPorNombreOApellido(String texto) {
        return usuarioRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(texto, texto).stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
    }

    public List<UsuarioDTO> buscarUsuariosPorDomicilio(String domicilio) {
        return usuarioRepository.findByDomicilioContainingIgnoreCase(domicilio).stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
    }

    private UsuarioDTO convertirAUsuarioDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCorreoElectronico(usuario.getCorreoElectronico());
        dto.setDomicilio(usuario.getDomicilio());
        dto.setTelefono(usuario.getTelefono());
        return dto;
    }
}