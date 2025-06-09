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

import com.Farmaline.Farmaline.dto.RepartidorDTO;
import com.Farmaline.Farmaline.service.RepartidorService;

@RestController
@RequestMapping("/api/repartidores")
public class RepartidorController {

    private final RepartidorService repartidorService;

    @Autowired
    public RepartidorController(RepartidorService repartidorService) {
        this.repartidorService = repartidorService;
    }

    @GetMapping
    public ResponseEntity<List<RepartidorDTO>> getAllRepartidores() {
        List<RepartidorDTO> repartidores = repartidorService.findAllRepartidores();
        return ResponseEntity.ok(repartidores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepartidorDTO> getRepartidorById(@PathVariable Integer id) {
        Optional<RepartidorDTO> repartidor = repartidorService.findRepartidorById(id);
        return repartidor.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/telefono")
    public ResponseEntity<RepartidorDTO> getRepartidorByTelefono(@RequestParam String telefono) {
        Optional<RepartidorDTO> repartidor = repartidorService.findRepartidorByTelefono(telefono);
        return repartidor.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/administrador/{adminId}")
    public ResponseEntity<List<RepartidorDTO>> getRepartidoresByAdministradorId(@PathVariable Integer adminId) {
        List<RepartidorDTO> repartidores = repartidorService.findRepartidoresByAdministradorId(adminId);
        if (repartidores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(repartidores);
    }

    @PostMapping
    public ResponseEntity<Object> createRepartidor(@RequestBody RepartidorDTO repartidorDTO) {
        try {
            RepartidorDTO createdRepartidor = repartidorService.createRepartidor(repartidorDTO);
            return new ResponseEntity<>(createdRepartidor, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // Para Vehículo/Admin no encontrado
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al crear repartidor: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateRepartidor(@PathVariable Integer id, @RequestBody RepartidorDTO repartidorDTO) {
        try {
            RepartidorDTO updatedRepartidor = repartidorService.updateRepartidor(id, repartidorDTO);
            return ResponseEntity.ok(updatedRepartidor);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRepartidor(@PathVariable Integer id) {
        try {
            repartidorService.deleteRepartidor(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateRepartidor(@RequestParam String telefono, @RequestParam String contrasena) {
        if (repartidorService.authenticateRepartidor(telefono, contrasena)) {
            return ResponseEntity.ok("Autenticación exitosa.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Teléfono o contraseña incorrectos.");
        }
    }
}