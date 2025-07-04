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

import com.farmaline.farmaline.dto.RepartidorDTO;
import com.farmaline.farmaline.service.RepartidorService;

@RestController
@RequestMapping("/api/repartidores")
public class RepartidorController {

    private final RepartidorService repartidorService;

    @Autowired
    public RepartidorController(RepartidorService repartidorService) {
        this.repartidorService = repartidorService;
    }

    @GetMapping
    public ResponseEntity<List<RepartidorDTO>> obtenerTodosRepartidores() {
        List<RepartidorDTO> repartidores = repartidorService.obtenerTodosRepartidores();
        return ResponseEntity.ok(repartidores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepartidorDTO> obtenerRepartidorPorId(@PathVariable Integer id) {
        return repartidorService.obtenerRepartidorPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RepartidorDTO> crearRepartidor(@RequestBody RepartidorDTO repartidorDTO) {
        try {
            RepartidorDTO nuevoRepartidor = repartidorService.crearRepartidor(repartidorDTO);
            return new ResponseEntity<>(nuevoRepartidor, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepartidorDTO> actualizarRepartidor(@PathVariable Integer id, @RequestBody RepartidorDTO repartidorDTO) {
        try {
            return repartidorService.actualizarRepartidor(id, repartidorDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRepartidor(@PathVariable Integer id) {
        if (repartidorService.eliminarRepartidor(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<RepartidorDTO> iniciarSesion(@RequestBody Map<String, String> credenciales) {
        String telefono = credenciales.get("telefono");
        String contrasena = credenciales.get("contrasena");
        return repartidorService.iniciarSesion(telefono, contrasena)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}