package com.farmaline.farmaline.controller;

import com.farmaline.farmaline.dto.UsuarioDTO;
import com.farmaline.farmaline.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> obtenerTodosUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.obtenerTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorId(@PathVariable Integer id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> crearUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        try {
            UsuarioDTO nuevoUsuario = usuarioService.crearUsuario(usuarioDTO);
            return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(@PathVariable Integer id, @RequestBody UsuarioDTO usuarioDTO) {
        return usuarioService.actualizarUsuario(id, usuarioDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) {
        if (usuarioService.eliminarUsuario(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> iniciarSesion(@RequestBody Map<String, String> credenciales) {
        String correoElectronico = credenciales.get("correoElectronico");
        String contrasena = credenciales.get("contrasena");
        return usuarioService.iniciarSesion(correoElectronico, contrasena)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}