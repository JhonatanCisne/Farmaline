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

import com.Farmaline.Farmaline.dto.Doble_VerificacionDTO;
import com.Farmaline.Farmaline.service.Doble_VerificacionService;

@RestController
@RequestMapping("/api/doble-verificacion")
public class Doble_VerificacionController {

    private final Doble_VerificacionService dobleVerificacionService;

    @Autowired
    public Doble_VerificacionController(Doble_VerificacionService dobleVerificacionService) {
        this.dobleVerificacionService = dobleVerificacionService;
    }

    @GetMapping
    public ResponseEntity<List<Doble_VerificacionDTO>> getAllDobleVerificaciones() {
        List<Doble_VerificacionDTO> verificaciones = dobleVerificacionService.findAllDobleVerificaciones();
        return ResponseEntity.ok(verificaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doble_VerificacionDTO> getDobleVerificacionById(@PathVariable Integer id) {
        Optional<Doble_VerificacionDTO> verificacion = dobleVerificacionService.findDobleVerificacionById(id);
        return verificacion.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<Doble_VerificacionDTO> getDobleVerificacionByPedidoId(@PathVariable Integer pedidoId) {
        Optional<Doble_VerificacionDTO> verificacion = dobleVerificacionService.findDobleVerificacionByPedidoId(pedidoId);
        return verificacion.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Doble_VerificacionDTO>> getDobleVerificacionesByUsuarioId(@PathVariable Integer usuarioId) {
        List<Doble_VerificacionDTO> verificaciones = dobleVerificacionService.findDobleVerificacionesByUsuarioId(usuarioId);
        if (verificaciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(verificaciones);
    }

    @GetMapping("/repartidor/{repartidorId}")
    public ResponseEntity<List<Doble_VerificacionDTO>> getDobleVerificacionesByRepartidorId(@PathVariable Integer repartidorId) {
        List<Doble_VerificacionDTO> verificaciones = dobleVerificacionService.findDobleVerificacionesByRepartidorId(repartidorId);
        if (verificaciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(verificaciones);
    }

    @PostMapping
    public ResponseEntity<Object> createDobleVerificacion(@RequestBody Doble_VerificacionDTO dobleVerificacionDTO) {
        try {
            Doble_VerificacionDTO createdVerificacion = dobleVerificacionService.createDobleVerificacion(dobleVerificacionDTO);
            return new ResponseEntity<>(createdVerificacion, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al crear doble verificación: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateDobleVerificacion(@PathVariable Integer id, @RequestBody Doble_VerificacionDTO dobleVerificacionDTO) {
        try {
            Doble_VerificacionDTO updatedVerificacion = dobleVerificacionService.updateDobleVerificacion(id, dobleVerificacionDTO);
            return ResponseEntity.ok(updatedVerificacion);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/estado-usuario")
    public ResponseEntity<Object> updateEstadoUsuario(@PathVariable Integer id, @RequestBody String nuevoEstado) {
        try {
            Doble_VerificacionDTO updatedVerificacion = dobleVerificacionService.updateEstadoUsuario(id, nuevoEstado);
            return ResponseEntity.ok(updatedVerificacion);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/estado-repartidor")
    public ResponseEntity<Object> updateEstadoRepartidor(@PathVariable Integer id, @RequestBody String nuevoEstado) {
        try {
            Doble_VerificacionDTO updatedVerificacion = dobleVerificacionService.updateEstadoRepartidor(id, nuevoEstado);
            return ResponseEntity.ok(updatedVerificacion);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDobleVerificacion(@PathVariable Integer id) {
        try {
            dobleVerificacionService.deleteDobleVerificacion(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}