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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Farmaline.Farmaline.dto.RegistroDTO;
import com.Farmaline.Farmaline.service.RegistroService;

@RestController
@RequestMapping("/api/registros")
public class RegistroController {

    private final RegistroService registroService;

    @Autowired
    public RegistroController(RegistroService registroService) {
        this.registroService = registroService;
    }

    @GetMapping
    public ResponseEntity<List<RegistroDTO>> getAllRegistros() {
        List<RegistroDTO> registros = registroService.findAllRegistros();
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroDTO> getRegistroById(@PathVariable Integer id) {
        Optional<RegistroDTO> registro = registroService.findRegistroById(id);
        return registro.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<RegistroDTO> getRegistroByPedidoId(@PathVariable Integer pedidoId) {
        Optional<RegistroDTO> registro = registroService.findRegistroByPedidoId(pedidoId);
        return registro.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/doble-verificacion/{dobleVerificacionId}")
    public ResponseEntity<RegistroDTO> getRegistroByDobleVerificacionId(@PathVariable Integer dobleVerificacionId) {
        Optional<RegistroDTO> registro = registroService.findRegistroByDobleVerificacionId(dobleVerificacionId);
        return registro.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Object> createRegistro(@RequestBody RegistroDTO registroDTO) {
        try {
            RegistroDTO createdRegistro = registroService.createRegistro(registroDTO);
            return new ResponseEntity<>(createdRegistro, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // Si se lanza por IDs no encontrados
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al crear el registro: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistro(@PathVariable Integer id) {
        try {
            registroService.deleteRegistro(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}