package com.Farmaline.Farmaline.controller;

import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Farmaline.Farmaline.dto.AdministradorDTO;
import com.Farmaline.Farmaline.dto.AdministradorLoginDTO;
import com.Farmaline.Farmaline.service.AdministradorService;

@RestController 
@RequestMapping("/api/administradores") 
public class AdministradorController {

    private final AdministradorService administradorService;

    @Autowired
    public AdministradorController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @GetMapping
    public ResponseEntity<List<AdministradorDTO>> getAllAdministradores() {
        List<AdministradorDTO> administradores = administradorService.findAllAdministradores();
        return ResponseEntity.ok(administradores); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministradorDTO> getAdministradorById(@PathVariable Integer id) {
        Optional<AdministradorDTO> administrador = administradorService.findAdministradorById(id);
        return administrador.map(ResponseEntity::ok) // Si encuentra el administrador, retorna 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build()); // Si no lo encuentra, retorna 404 Not Found
    }

    @PostMapping("/registro")
    public ResponseEntity<?> createAdministrador(@RequestBody AdministradorDTO administradorDTO, @RequestParam String contrasena) {
        try {
            AdministradorDTO createdAdministrador = administradorService.createAdministrador(administradorDTO, contrasena);
            return new ResponseEntity<>(createdAdministrador, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor al crear administrador: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdministradorDTO> updateAdministrador(@PathVariable Integer id, @RequestBody AdministradorDTO administradorDTO) {
        try {
            AdministradorDTO updatedAdministrador = administradorService.updateAdministrador(id, administradorDTO);
            return ResponseEntity.ok(updatedAdministrador); 
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/contrasena")
    public ResponseEntity<?> updateAdministradorPassword(@PathVariable Integer id, @RequestParam String newPassword) {
        try {
            administradorService.updateAdministradorPassword(id, newPassword);
            return ResponseEntity.ok("Contraseña de administrador actualizada exitosamente."); // Retorna 200 OK
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); 
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); 
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdministrador(@PathVariable Integer id) {
        try {
            administradorService.deleteAdministrador(id);
            return ResponseEntity.noContent().build(); 
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginAdministrador(@RequestBody AdministradorLoginDTO loginDTO) {
        Optional<AdministradorDTO> authenticatedAdministrador = administradorService.authenticateAdministrador(loginDTO);
        if (authenticatedAdministrador.isPresent()) {
            return ResponseEntity.ok(authenticatedAdministrador.get()); 
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nombre de usuario o contraseña incorrectos."); 
        }
    }
}