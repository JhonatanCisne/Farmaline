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

    @Autowired
    private CalificacionService calificacionService;

    @GetMapping
    public ResponseEntity<List<CalificacionDTO>> getAllCalificaciones() {
        List<CalificacionDTO> calificaciones = calificacionService.getAllCalificaciones();
        return ResponseEntity.ok(calificaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalificacionDTO> getCalificacionById(@PathVariable Integer id) {
        return calificacionService.getCalificacionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<CalificacionDTO>> getCalificacionesByProductId(@PathVariable Integer idProducto) {
        List<CalificacionDTO> calificaciones = calificacionService.getCalificacionesByProductId(idProducto);
        return ResponseEntity.ok(calificaciones);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<CalificacionDTO>> getCalificacionesByUserId(@PathVariable Integer idUsuario) {
        List<CalificacionDTO> calificaciones = calificacionService.getCalificacionesByUserId(idUsuario);
        return ResponseEntity.ok(calificaciones);
    }

    @GetMapping("/usuario/{idUsuario}/producto/{idProducto}")
    public ResponseEntity<CalificacionDTO> getCalificacionByUserIdAndProductId(
            @PathVariable Integer idUsuario,
            @PathVariable Integer idProducto) {
        return calificacionService.getCalificacionByUserIdAndProductId(idUsuario, idProducto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/producto/{idProducto}/puntuacion/mayor-igual/{puntuacion}")
    public ResponseEntity<List<CalificacionDTO>> getCalificacionesGreaterThanEqual(
            @PathVariable Integer idProducto,
            @PathVariable Integer puntuacion) {
        List<CalificacionDTO> calificaciones = calificacionService.getCalificacionesGreaterThanEqual(idProducto, puntuacion);
        return ResponseEntity.ok(calificaciones);
    }

    @GetMapping("/producto/{idProducto}/puntuacion/menor-igual/{puntuacion}")
    public ResponseEntity<List<CalificacionDTO>> getCalificacionesLessThanEqual(
            @PathVariable Integer idProducto,
            @PathVariable Integer puntuacion) {
        List<CalificacionDTO> calificaciones = calificacionService.getCalificacionesLessThanEqual(idProducto, puntuacion);
        return ResponseEntity.ok(calificaciones);
    }

    @GetMapping("/producto/{idProducto}/promedio")
    public ResponseEntity<Double> getAverageRatingForProduct(@PathVariable Integer idProducto) {
        Double averageRating = calificacionService.getAverageRatingForProduct(idProducto);
        return ResponseEntity.ok(averageRating);
    }

    @PostMapping
    public ResponseEntity<CalificacionDTO> createCalificacion(@RequestBody CalificacionDTO calificacionDTO) {
        try {
            CalificacionDTO createdCalificacion = calificacionService.createCalificacion(calificacionDTO);
            return new ResponseEntity<>(createdCalificacion, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CalificacionDTO> updateCalificacion(@PathVariable Integer id, @RequestBody CalificacionDTO calificacionDTO) {
        try {
            return calificacionService.updateCalificacion(id, calificacionDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCalificacion(@PathVariable Integer id) {
        if (calificacionService.deleteCalificacion(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/producto/{idProducto}")
    public ResponseEntity<Void> deleteCalificacionesByProductId(@PathVariable Integer idProducto) {
        calificacionService.deleteCalificacionesByProductId(idProducto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/usuario/{idUsuario}")
    public ResponseEntity<Void> deleteCalificacionesByUserId(@PathVariable Integer idUsuario) {
        calificacionService.deleteCalificacionesByUserId(idUsuario);
        return ResponseEntity.noContent().build();
    }
}