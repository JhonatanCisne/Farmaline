package com.farmaline.farmaline.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmaline.farmaline.dto.RegistroDTO;
import com.farmaline.farmaline.service.RegistroService;

@RestController
@RequestMapping("/api/registros")
public class RegistroController {

    @Autowired
    private RegistroService registroService;

    @GetMapping
    public ResponseEntity<List<RegistroDTO>> getAllRegistros() {
        List<RegistroDTO> registros = registroService.getAllRegistros();
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroDTO> getRegistroById(@PathVariable Integer id) {
        return registroService.getRegistroById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<RegistroDTO> getRegistroByPedidoId(@PathVariable Integer idPedido) {
        return registroService.getRegistroByPedidoId(idPedido)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/doble-verificacion/{idDobleVerificacion}")
    public ResponseEntity<RegistroDTO> getRegistroByDobleVerificacionId(@PathVariable Integer idDobleVerificacion) {
        return registroService.getRegistroByDobleVerificacionId(idDobleVerificacion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RegistroDTO> createRegistro(@RequestBody RegistroDTO registroDTO) {
        try {
            RegistroDTO createdRegistro = registroService.createRegistro(registroDTO);
            return new ResponseEntity<>(createdRegistro, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistro(@PathVariable Integer id) {
        if (registroService.deleteRegistro(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/pedido/{idPedido}")
    public ResponseEntity<Void> deleteRegistroByPedidoId(@PathVariable Integer idPedido) {
        if (registroService.deleteRegistroByPedidoId(idPedido)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/doble-verificacion/{idDobleVerificacion}")
    public ResponseEntity<Void> deleteRegistroByDobleVerificacionId(@PathVariable Integer idDobleVerificacion) {
        if (registroService.deleteRegistroByDobleVerificacionId(idDobleVerificacion)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}