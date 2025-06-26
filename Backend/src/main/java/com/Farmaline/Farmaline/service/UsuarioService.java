package com.farmaline.farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.dto.UsuarioDTO;
import com.farmaline.farmaline.model.Carrito;
import com.farmaline.farmaline.model.Usuario;
import com.farmaline.farmaline.repository.CarritoRepository;
import com.farmaline.farmaline.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CarritoRepository carritoRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, CarritoRepository carritoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.carritoRepository = carritoRepository;
    }

    public List<UsuarioDTO> obtenerTodosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
    }

    public Optional<UsuarioDTO> obtenerUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id)
                .map(this::convertirAUsuarioDTO);
    }

    @Transactional
    public UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());
        usuario.setCorreoElectronico(usuarioDTO.getCorreoElectronico());
        usuario.setDomicilio(usuarioDTO.getDomicilio());
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setContrasena(usuarioDTO.getContrasena());

        usuario = usuarioRepository.save(usuario);

        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carritoRepository.save(carrito);

        return convertirAUsuarioDTO(usuario);
    }

    @Transactional
    public Optional<UsuarioDTO> actualizarUsuario(Integer id, UsuarioDTO usuarioDTO) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    usuarioExistente.setNombre(usuarioDTO.getNombre());
                    usuarioExistente.setApellido(usuarioDTO.getApellido());
                    usuarioExistente.setCorreoElectronico(usuarioDTO.getCorreoElectronico());
                    usuarioExistente.setDomicilio(usuarioDTO.getDomicilio());
                    usuarioExistente.setTelefono(usuarioDTO.getTelefono());
                    usuarioExistente.setContrasena(usuarioDTO.getContrasena());
                    return convertirAUsuarioDTO(usuarioRepository.save(usuarioExistente));
                });
    }

    public boolean eliminarUsuario(Integer id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<UsuarioDTO> iniciarSesion(String correoElectronico, String contrasena) {
        return usuarioRepository.findByCorreoElectronicoAndContrasena(correoElectronico, contrasena)
                .map(this::convertirAUsuarioDTO);
    }

    private UsuarioDTO convertirAUsuarioDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCorreoElectronico(usuario.getCorreoElectronico());
        dto.setDomicilio(usuario.getDomicilio());
        dto.setTelefono(usuario.getTelefono());
        dto.setContrasena(usuario.getContrasena());
        // Se asume que el Carrito ya existe y está asociado al usuario
        carritoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario()).ifPresent(carrito -> dto.setIdCarrito(carrito.getIdCarrito()));
        return dto;
    }
}