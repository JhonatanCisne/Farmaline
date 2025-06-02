package com.Farmaline.Farmaline.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Asegúrate de que esta importación sea correcta
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Farmaline.Farmaline.dto.UsuarioDTO;
import com.Farmaline.Farmaline.dto.UsuarioLoginDTO;
import com.Farmaline.Farmaline.dto.UsuarioRegistroDTO;
import com.Farmaline.Farmaline.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService; 

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioDTO> registrarUsuario(@RequestBody UsuarioRegistroDTO usuarioRegistroDTO) {
        try {
            UsuarioDTO nuevoUsuario = usuarioService.registrarUsuario(usuarioRegistroDTO);
            return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> loginUsuario(@RequestBody UsuarioLoginDTO usuarioLoginDTO) {
        Optional<UsuarioDTO> usuario = usuarioService.loginUsuario(usuarioLoginDTO);
        return usuario.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorId(@PathVariable Integer id) {
        Optional<UsuarioDTO> usuario = usuarioService.obtenerUsuarioPorId(id);
        return usuario.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(@PathVariable Integer id, @RequestBody UsuarioDTO usuarioDTO) {
        try {
            UsuarioDTO usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioDTO);
            return new ResponseEntity<>(usuarioActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Manejo para usuario no encontrado al actualizar
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) {
        try {
            usuarioService.eliminarUsuario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content para eliminación exitosa
        } catch (RuntimeException e) {
            // Manejo para usuario no encontrado al eliminar
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> obtenerTodosLosUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioDTO>> buscarUsuarios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String texto, // Para buscar por nombre O apellido
            @RequestParam(required = false) String domicilio) {

        List<UsuarioDTO> usuarios;
        if (nombre != null && !nombre.isEmpty()) {
            usuarios = usuarioService.buscarUsuariosPorNombre(nombre);
        } else if (apellido != null && !apellido.isEmpty()) {
            usuarios = usuarioService.buscarUsuariosPorApellido(apellido);
        } else if (texto != null && !texto.isEmpty()) {
            usuarios = usuarioService.buscarUsuariosPorNombreOApellido(texto);
        } else if (domicilio != null && !domicilio.isEmpty()) {
            usuarios = usuarioService.buscarUsuariosPorDomicilio(domicilio);
        } else {
            // Si no se proporciona ningún parámetro, devolver todos los usuarios o un BAD_REQUEST según la lógica de negocio
            usuarios = usuarioService.obtenerTodosLosUsuarios();
        }
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }
}