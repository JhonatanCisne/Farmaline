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

import com.farmaline.farmaline.dto.RepartidorDTO;
import com.farmaline.farmaline.service.RepartidorService;

@RestController
@RequestMapping("/api/repartidores")
public class RepartidorController {

    @Autowired
    private RepartidorService repartidorService;

    @GetMapping
    public ResponseEntity<List<RepartidorDTO>> getAllRepartidores() {
        List<RepartidorDTO> repartidores = repartidorService.getAllRepartidores();
        return ResponseEntity.ok(repartidores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepartidorDTO> getRepartidorById(@PathVariable Integer id) {
        return repartidorService.getRepartidorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RepartidorDTO> createRepartidor(@RequestBody RepartidorDTO repartidorDTO) {
        try {
            RepartidorDTO createdRepartidor = repartidorService.createRepartidor(repartidorDTO);
            return new ResponseEntity<>(createdRepartidor, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepartidorDTO> updateRepartidor(@PathVariable Integer id, @RequestBody RepartidorDTO repartidorDTO) {
        try {
            return repartidorService.updateRepartidor(id, repartidorDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRepartidor(@PathVariable Integer id) {
        if (repartidorService.deleteRepartidor(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<RepartidorDTO> authenticateRepartidor(@RequestBody RepartidorDTO loginRequest) {
        String correoElectronico = loginRequest.getCorreo_Electronico();
        String contrasena = loginRequest.getContrasena();

        return repartidorService.authenticateRepartidor(correoElectronico, contrasena)
                .map(repartidorDTO -> ResponseEntity.ok(repartidorDTO))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}