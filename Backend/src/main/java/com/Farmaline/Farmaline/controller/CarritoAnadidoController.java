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

import com.farmaline.farmaline.dto.CarritoAnadidoDTO;
import com.farmaline.farmaline.service.CarritoAnadidoService;

@RestController
@RequestMapping("/api/carrito-anadidos")
public class CarritoAnadidoController {

    @Autowired
    private CarritoAnadidoService carritoAnadidoService;

    @GetMapping
    public ResponseEntity<List<CarritoAnadidoDTO>> getAllCarritoAnadidos() {
        List<CarritoAnadidoDTO> carritoAnadidos = carritoAnadidoService.getAllCarritoAnadidos();
        return ResponseEntity.ok(carritoAnadidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarritoAnadidoDTO> getCarritoAnadidoById(@PathVariable Integer id) {
        return carritoAnadidoService.getCarritoAnadidoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/carrito/{idCarrito}")
    public ResponseEntity<List<CarritoAnadidoDTO>> getCarritoAnadidosByCarritoId(@PathVariable Integer idCarrito) {
        List<CarritoAnadidoDTO> carritoAnadidos = carritoAnadidoService.getCarritoAnadidosByCarritoId(idCarrito);
        return ResponseEntity.ok(carritoAnadidos);
    }

    @PostMapping
    public ResponseEntity<CarritoAnadidoDTO> addProductoToCarrito(@RequestBody CarritoAnadidoDTO carritoAnadidoDTO) {
        try {
            CarritoAnadidoDTO addedItem = carritoAnadidoService.addProductoToCarrito(carritoAnadidoDTO);
            return new ResponseEntity<>(addedItem, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{idCarritoAnadido}/cantidad/{newCantidad}")
    public ResponseEntity<CarritoAnadidoDTO> updateCantidadInCarrito(
            @PathVariable Integer idCarritoAnadido,
            @PathVariable int newCantidad) {
        try {
            return carritoAnadidoService.updateCantidadInCarrito(idCarritoAnadido, newCantidad)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/carrito/{idCarrito}/producto/{idProducto}")
    public ResponseEntity<Void> removeProductoFromCarrito(
            @PathVariable Integer idCarrito,
            @PathVariable Integer idProducto) {
        if (carritoAnadidoService.removeProductoFromCarrito(idCarrito, idProducto)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/carrito/{idCarrito}/clear")
    public ResponseEntity<Void> clearCarrito(@PathVariable Integer idCarrito) {
        carritoAnadidoService.clearCarrito(idCarrito);
        return ResponseEntity.noContent().build();
    }
}