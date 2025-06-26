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

import com.farmaline.farmaline.dto.CarritoDTO;
import com.farmaline.farmaline.service.CarritoService;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final CarritoService carritoService;

    @Autowired
    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public ResponseEntity<List<CarritoDTO>> obtenerTodosCarritos() {
        List<CarritoDTO> carritos = carritoService.obtenerTodosCarritos();
        return ResponseEntity.ok(carritos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarritoDTO> obtenerCarritoPorId(@PathVariable Integer id) {
        return carritoService.obtenerCarritoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<CarritoDTO> obtenerCarritoPorIdUsuario(@PathVariable Integer idUsuario) {
        return carritoService.obtenerCarritoPorIdUsuario(idUsuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CarritoDTO> crearCarrito(@RequestBody CarritoDTO carritoDTO) {
        try {
            CarritoDTO nuevoCarrito = carritoService.crearCarrito(carritoDTO);
            return new ResponseEntity<>(nuevoCarrito, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarritoDTO> actualizarCarrito(@PathVariable Integer id, @RequestBody CarritoDTO carritoDTO) {
        try {
            return carritoService.actualizarCarrito(id, carritoDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCarrito(@PathVariable Integer id) {
        if (carritoService.eliminarCarrito(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}