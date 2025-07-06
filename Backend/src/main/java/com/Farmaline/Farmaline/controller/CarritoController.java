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

import com.farmaline.farmaline.dto.CarritoDTO;
import com.farmaline.farmaline.service.CarritoService;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping
    public ResponseEntity<List<CarritoDTO>> getAllCarritos() {
        List<CarritoDTO> carritos = carritoService.getAllCarritos();
        return ResponseEntity.ok(carritos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarritoDTO> getCarritoById(@PathVariable Integer id) {
        return carritoService.getCarritoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<CarritoDTO> getCarritoByUserId(@PathVariable Integer idUsuario) {
        return carritoService.getCarritoByUserId(idUsuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CarritoDTO> createCarrito(@RequestBody CarritoDTO carritoDTO) {
        try {
            CarritoDTO createdCarrito = carritoService.createCarrito(carritoDTO.getIdUsuario());
            return new ResponseEntity<>(createdCarrito, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{idCarrito}")
    public ResponseEntity<Void> deleteCarrito(@PathVariable Integer idCarrito) {
        if (carritoService.deleteCarrito(idCarrito)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/usuario/{idUsuario}")
    public ResponseEntity<Void> deleteCarritoByUserId(@PathVariable Integer idUsuario) {
        if (carritoService.deleteCarritoByUserId(idUsuario)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}