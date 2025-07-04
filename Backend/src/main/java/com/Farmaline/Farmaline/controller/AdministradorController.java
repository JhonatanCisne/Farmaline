package com.Farmaline.farmaline.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmaline.farmaline.dto.AdministradorDTO;
import com.farmaline.farmaline.service.AdministradorService;

@RestController
@RequestMapping("/api/administradores")
public class AdministradorController {

    private final AdministradorService administradorService;

    @Autowired
    public AdministradorController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @GetMapping
    public ResponseEntity<List<AdministradorDTO>> obtenerTodosAdministradores() {
        List<AdministradorDTO> administradores = administradorService.obtenerTodosAdministradores();
        return ResponseEntity.ok(administradores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministradorDTO> obtenerAdministradorPorId(@PathVariable Integer id) {
        return administradorService.obtenerAdministradorPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AdministradorDTO> crearAdministrador(@RequestBody AdministradorDTO administradorDTO) {
        AdministradorDTO nuevoAdministrador = administradorService.crearAdministrador(administradorDTO);
        return new ResponseEntity<>(nuevoAdministrador, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdministradorDTO> actualizarAdministrador(@PathVariable Integer id, @RequestBody AdministradorDTO administradorDTO) {
        return administradorService.actualizarAdministrador(id, administradorDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAdministrador(@PathVariable Integer id) {
        if (administradorService.eliminarAdministrador(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AdministradorDTO> iniciarSesion(@RequestBody Map<String, String> credenciales) {
        String nombre = credenciales.get("nombre");
        String contrasena = credenciales.get("contrasena");
        return administradorService.iniciarSesion(nombre, contrasena)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}