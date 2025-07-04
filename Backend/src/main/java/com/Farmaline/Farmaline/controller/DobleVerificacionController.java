package com.Farmaline.farmaline.controller;

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

import com.farmaline.farmaline.dto.DobleVerificacionDTO;
import com.farmaline.farmaline.service.DobleVerificacionService;

@RestController
@RequestMapping("/api/doble-verificacion")
public class DobleVerificacionController {

    private final DobleVerificacionService dobleVerificacionService;

    @Autowired
    public DobleVerificacionController(DobleVerificacionService dobleVerificacionService) {
        this.dobleVerificacionService = dobleVerificacionService;
    }

    @GetMapping
    public ResponseEntity<List<DobleVerificacionDTO>> obtenerTodasDobleVerificaciones() {
        List<DobleVerificacionDTO> dobleVerificaciones = dobleVerificacionService.obtenerTodasDobleVerificaciones();
        return ResponseEntity.ok(dobleVerificaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DobleVerificacionDTO> obtenerDobleVerificacionPorId(@PathVariable Integer id) {
        return dobleVerificacionService.obtenerDobleVerificacionPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DobleVerificacionDTO> crearDobleVerificacion(@RequestBody DobleVerificacionDTO dobleVerificacionDTO) {
        try {
            DobleVerificacionDTO nuevaDobleVerificacion = dobleVerificacionService.crearDobleVerificacion(dobleVerificacionDTO);
            return new ResponseEntity<>(nuevaDobleVerificacion, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DobleVerificacionDTO> actualizarDobleVerificacion(@PathVariable Integer id, @RequestBody DobleVerificacionDTO dobleVerificacionDTO) {
        try {
            return dobleVerificacionService.actualizarDobleVerificacion(id, dobleVerificacionDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDobleVerificacion(@PathVariable Integer id) {
        if (dobleVerificacionService.eliminarDobleVerificacion(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}