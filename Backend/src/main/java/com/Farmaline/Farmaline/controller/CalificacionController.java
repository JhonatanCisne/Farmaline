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

import com.farmaline.farmaline.dto.CalificacionDTO;
import com.farmaline.farmaline.service.CalificacionService;

@RestController
@RequestMapping("/api/calificaciones")
public class CalificacionController {

    private final CalificacionService calificacionService;

    @Autowired
    public CalificacionController(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;
    }

    @GetMapping
    public ResponseEntity<List<CalificacionDTO>> obtenerTodasCalificaciones() {
        List<CalificacionDTO> calificaciones = calificacionService.obtenerTodasCalificaciones();
        return ResponseEntity.ok(calificaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalificacionDTO> obtenerCalificacionPorId(@PathVariable Integer id) {
        return calificacionService.obtenerCalificacionPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CalificacionDTO> crearCalificacion(@RequestBody CalificacionDTO calificacionDTO) {
        try {
            CalificacionDTO nuevaCalificacion = calificacionService.crearCalificacion(calificacionDTO);
            return new ResponseEntity<>(nuevaCalificacion, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CalificacionDTO> actualizarCalificacion(@PathVariable Integer id, @RequestBody CalificacionDTO calificacionDTO) {
        try {
            return calificacionService.actualizarCalificacion(id, calificacionDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCalificacion(@PathVariable Integer id) {
        if (calificacionService.eliminarCalificacion(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}