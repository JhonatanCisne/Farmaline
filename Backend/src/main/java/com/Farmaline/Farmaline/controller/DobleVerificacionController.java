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

import com.farmaline.farmaline.dto.DobleVerificacionDTO;
import com.farmaline.farmaline.service.DobleVerificacionService;

@RestController
@RequestMapping("/api/doble-verificaciones")
public class DobleVerificacionController {

    @Autowired
    private DobleVerificacionService dobleVerificacionService;

    @GetMapping
    public ResponseEntity<List<DobleVerificacionDTO>> getAllDobleVerificaciones() {
        List<DobleVerificacionDTO> verificaciones = dobleVerificacionService.getAllDobleVerificaciones();
        return ResponseEntity.ok(verificaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DobleVerificacionDTO> getDobleVerificacionById(@PathVariable Integer id) {
        return dobleVerificacionService.getDobleVerificacionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<DobleVerificacionDTO> getDobleVerificacionByPedidoId(@PathVariable Integer idPedido) {
        return dobleVerificacionService.getDobleVerificacionByPedidoId(idPedido)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<DobleVerificacionDTO>> getDobleVerificacionesByUsuarioId(@PathVariable Integer idUsuario) {
        List<DobleVerificacionDTO> verificaciones = dobleVerificacionService.getDobleVerificacionesByUsuarioId(idUsuario);
        return ResponseEntity.ok(verificaciones);
    }

    @GetMapping("/repartidor/{idRepartidor}")
    public ResponseEntity<List<DobleVerificacionDTO>> getDobleVerificacionesByRepartidorId(@PathVariable Integer idRepartidor) {
        List<DobleVerificacionDTO> verificaciones = dobleVerificacionService.getDobleVerificacionesByRepartidorId(idRepartidor);
        return ResponseEntity.ok(verificaciones);
    }

    @PostMapping
    public ResponseEntity<DobleVerificacionDTO> createDobleVerificacion(@RequestBody DobleVerificacionDTO dobleVerificacionDTO) {
        try {
            DobleVerificacionDTO createdVerificacion = dobleVerificacionService.createDobleVerificacion(dobleVerificacionDTO);
            return new ResponseEntity<>(createdVerificacion, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{idDobleVerificacion}/estado-usuario")
    public ResponseEntity<DobleVerificacionDTO> updateEstadoUsuario(
            @PathVariable Integer idDobleVerificacion,
            @RequestBody String nuevoEstado) { 
        return dobleVerificacionService.updateEstadoUsuario(idDobleVerificacion, nuevoEstado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idDobleVerificacion}/estado-repartidor")
    public ResponseEntity<DobleVerificacionDTO> updateEstadoRepartidor(
            @PathVariable Integer idDobleVerificacion,
            @RequestBody String nuevoEstado) { // Considera usar un DTO específico si el cuerpo es más complejo
        return dobleVerificacionService.updateEstadoRepartidor(idDobleVerificacion, nuevoEstado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDobleVerificacion(@PathVariable Integer id) {
        if (dobleVerificacionService.deleteDobleVerificacion(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}