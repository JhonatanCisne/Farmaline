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

import com.farmaline.farmaline.dto.RegistroDTO;
import com.farmaline.farmaline.service.RegistroService;

@RestController
@RequestMapping("/api/registros")
public class RegistroController {

    private final RegistroService registroService;

    @Autowired
    public RegistroController(RegistroService registroService) {
        this.registroService = registroService;
    }

    @GetMapping
    public ResponseEntity<List<RegistroDTO>> obtenerTodosRegistros() {
        List<RegistroDTO> registros = registroService.obtenerTodosRegistros();
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroDTO> obtenerRegistroPorId(@PathVariable Integer id) {
        return registroService.obtenerRegistroPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RegistroDTO> crearRegistro(@RequestBody RegistroDTO registroDTO) {
        try {
            RegistroDTO nuevoRegistro = registroService.crearRegistro(registroDTO);
            return new ResponseEntity<>(nuevoRegistro, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroDTO> actualizarRegistro(@PathVariable Integer id, @RequestBody RegistroDTO registroDTO) {
        try {
            return registroService.actualizarRegistro(id, registroDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRegistro(@PathVariable Integer id) {
        if (registroService.eliminarRegistro(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}