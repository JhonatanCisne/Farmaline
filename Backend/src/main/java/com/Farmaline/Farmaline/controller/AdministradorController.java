package com.farmaline.farmaline.controller;

import java.util.List;

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

    @Autowired
    private AdministradorService administradorService;

    @GetMapping
    public ResponseEntity<List<AdministradorDTO>> getAllAdministradores() {
        List<AdministradorDTO> administradores = administradorService.getAllAdministradores();
        return ResponseEntity.ok(administradores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministradorDTO> getAdministradorById(@PathVariable Integer id) {
        return administradorService.getAdministradorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AdministradorDTO> createAdministrador(@RequestBody AdministradorDTO administradorDTO) {
        try {
            AdministradorDTO createdAdministrador = administradorService.createAdministrador(administradorDTO);
            return new ResponseEntity<>(createdAdministrador, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdministradorDTO> updateAdministrador(@PathVariable Integer id, @RequestBody AdministradorDTO administradorDTO) {
        try {
            return administradorService.updateAdministrador(id, administradorDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdministrador(@PathVariable Integer id) {
        if (administradorService.deleteAdministrador(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AdministradorDTO> authenticateAdministrador(@RequestBody AdministradorDTO loginRequest) {
        String usuario = loginRequest.getUsuario();
        String contrasena = loginRequest.getContrasena();

        return administradorService.authenticateAdministrador(usuario, contrasena)
                .map(administradorDTO -> ResponseEntity.ok(administradorDTO))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}