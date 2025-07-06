package com.farmaline.farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.dto.UsuarioDTO; 
import com.farmaline.farmaline.model.Usuario;
import com.farmaline.farmaline.repository.CalificacionRepository;
import com.farmaline.farmaline.repository.CarritoRepository;
import com.farmaline.farmaline.repository.DobleVerificacionRepository;
import com.farmaline.farmaline.repository.PedidoRepository;
import com.farmaline.farmaline.repository.RegistroRepository;
import com.farmaline.farmaline.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CarritoRepository carritoRepository; 
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private CalificacionRepository calificacionRepository;
    @Autowired
    private RegistroRepository registroRepository; 
    @Autowired
    private DobleVerificacionRepository dobleVerificacionRepository; 

    public List<UsuarioDTO> getAllUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UsuarioDTO> getUsuarioById(Integer id) {
        return usuarioRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional
    public UsuarioDTO createUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByCorreoElectronico(usuarioDTO.getCorreoElectronico())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }
        if (usuarioRepository.existsByTelefono(usuarioDTO.getTelefono())) {
            throw new IllegalArgumentException("El teléfono ya está registrado.");
        }

        Usuario usuario = convertToEntity(usuarioDTO);
        usuario.setContrasena(usuarioDTO.getContrasena());
        Usuario savedUsuario = usuarioRepository.save(usuario);
        return convertToDTO(savedUsuario);
    }

    @Transactional
    public Optional<UsuarioDTO> updateUsuario(Integer id, UsuarioDTO usuarioDTO) {
        return usuarioRepository.findById(id).map(existingUsuario -> {
            if (!existingUsuario.getCorreoElectronico().equals(usuarioDTO.getCorreoElectronico()) &&
                usuarioRepository.existsByCorreoElectronico(usuarioDTO.getCorreoElectronico())) {
                throw new IllegalArgumentException("El nuevo correo electrónico ya está en uso.");
            }
            if (!existingUsuario.getTelefono().equals(usuarioDTO.getTelefono()) &&
                usuarioRepository.existsByTelefono(usuarioDTO.getTelefono())) {
                throw new IllegalArgumentException("El nuevo teléfono ya está en uso.");
            }

            existingUsuario.setNombre(usuarioDTO.getNombre());
            existingUsuario.setApellido(usuarioDTO.getApellido());
            existingUsuario.setCorreoElectronico(usuarioDTO.getCorreoElectronico());
            existingUsuario.setDomicilio(usuarioDTO.getDomicilio());
            existingUsuario.setTelefono(usuarioDTO.getTelefono());

            if (usuarioDTO.getContrasena() != null && !usuarioDTO.getContrasena().isEmpty()) {
                existingUsuario.setContrasena(usuarioDTO.getContrasena()); 
            }

            Usuario updatedUsuario = usuarioRepository.save(existingUsuario);
            return convertToDTO(updatedUsuario);
        });
    }

    @Transactional
    public boolean deleteUsuario(Integer id) {
        if (usuarioRepository.existsById(id)) {

            calificacionRepository.deleteByUsuario_IdUsuario(id);

            dobleVerificacionRepository.deleteByUsuario_IdUsuario(id);

            pedidoRepository.deleteByUsuario_IdUsuario(id);

            carritoRepository.deleteByUsuario_IdUsuario(id);

            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<UsuarioDTO> authenticateUsuario(String correoElectronico, String contrasena) {
        return usuarioRepository.findByCorreoElectronico(correoElectronico)
                .filter(usuario -> contrasena.equals(usuario.getContrasena())) 
                .map(this::convertToDTO);
    }

    private UsuarioDTO convertToDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCorreoElectronico(usuario.getCorreoElectronico());
        dto.setDomicilio(usuario.getDomicilio());
        dto.setTelefono(usuario.getTelefono());
        dto.setContrasena(usuario.getContrasena());
        return dto;
    }

    private Usuario convertToEntity(UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(usuarioDTO.getIdUsuario()); // Puede ser null para nuevas entidades
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());
        usuario.setCorreoElectronico(usuarioDTO.getCorreoElectronico());
        usuario.setDomicilio(usuarioDTO.getDomicilio());
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setContrasena(usuarioDTO.getContrasena()); // Contraseña se setea aquí para consistencia
        return usuario;
    }
}