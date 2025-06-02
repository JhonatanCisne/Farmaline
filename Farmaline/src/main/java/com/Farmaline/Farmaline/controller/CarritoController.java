package com.Farmaline.Farmaline.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Farmaline.Farmaline.dto.CarritoDTO;
import com.Farmaline.Farmaline.service.CarritoService;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final CarritoService carritoService;

    @Autowired
    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public ResponseEntity<List<CarritoDTO>> getAllCarritos() {
        List<CarritoDTO> carritos = carritoService.findAllCarritos();
        return ResponseEntity.ok(carritos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarritoDTO> getCarritoById(@PathVariable Integer id) {
        Optional<CarritoDTO> carrito = carritoService.findCarritoById(id);
        return carrito.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CarritoDTO> getCarritoByUsuarioId(@PathVariable Integer usuarioId) {
        Optional<CarritoDTO> carrito = carritoService.findCarritoByUsuarioId(usuarioId);
        return carrito.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Object> createCarrito(@RequestBody CarritoDTO carritoDTO) {
        try {
            CarritoDTO createdCarrito = carritoService.createCarrito(carritoDTO);
            return new ResponseEntity<>(createdCarrito, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al crear carrito: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}