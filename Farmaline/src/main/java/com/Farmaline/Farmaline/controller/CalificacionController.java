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
import org.springframework.web.bind.annotation.RestController;

import com.Farmaline.Farmaline.dto.CalificacionDTO;
import com.Farmaline.Farmaline.service.CalificacionService;

@RestController
@RequestMapping("/api/calificaciones")
public class CalificacionController {

    private final CalificacionService calificacionService;

    @Autowired
    public CalificacionController(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;
    }

    @GetMapping
    public ResponseEntity<List<CalificacionDTO>> getAllCalificaciones() {
        List<CalificacionDTO> calificaciones = calificacionService.findAllCalificaciones();
        return ResponseEntity.ok(calificaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalificacionDTO> getCalificacionById(@PathVariable Integer id) {
        Optional<CalificacionDTO> calificacion = calificacionService.findCalificacionById(id);
        return calificacion.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<CalificacionDTO>> getCalificacionesByProductoId(@PathVariable Integer productoId) {
        List<CalificacionDTO> calificaciones = calificacionService.findCalificacionesByProductoId(productoId);
        if (calificaciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(calificaciones);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CalificacionDTO>> getCalificacionesByUsuarioId(@PathVariable Integer usuarioId) {
        List<CalificacionDTO> calificaciones = calificacionService.findCalificacionesByUsuarioId(usuarioId);
        if (calificaciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(calificaciones);
    }

    @PostMapping
    public ResponseEntity<?> createCalificacion(@RequestBody CalificacionDTO calificacionDTO) {
        try {
            CalificacionDTO createdCalificacion = calificacionService.saveCalificacion(calificacionDTO);
            return new ResponseEntity<>(createdCalificacion, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor al crear calificación: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CalificacionDTO> updateCalificacion(@PathVariable Integer id, @RequestBody CalificacionDTO calificacionDTO) {
        try {
            CalificacionDTO updatedCalificacion = calificacionService.updateCalificacion(id, calificacionDTO);
            return ResponseEntity.ok(updatedCalificacion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCalificacion(@PathVariable Integer id) {
        try {
            calificacionService.deleteCalificacionById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}