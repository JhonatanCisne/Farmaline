package com.Farmaline.Farmaline.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Farmaline.Farmaline.dto.Carrito_AnadidoDTO;
import com.Farmaline.Farmaline.service.Carrito_AnadidoService;

@RestController
@RequestMapping("/api/carrito-anadido")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class CarritoAnadidoController {

    private final Carrito_AnadidoService carritoAnadidoService;

    @Autowired
    public CarritoAnadidoController(Carrito_AnadidoService carritoAnadidoService) {
        this.carritoAnadidoService = carritoAnadidoService;
    }

    @GetMapping
    public ResponseEntity<List<Carrito_AnadidoDTO>> getAllCarritoAnadidos() {
        List<Carrito_AnadidoDTO> items = carritoAnadidoService.findAllCarritoAnadidos();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carrito_AnadidoDTO> getCarritoAnadidoById(@PathVariable Integer id) {
        Optional<Carrito_AnadidoDTO> item = carritoAnadidoService.findCarritoAnadidoById(id);
        return item.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/carrito/{carritoId}")
    public ResponseEntity<List<Carrito_AnadidoDTO>> getCarritoAnadidosByCarritoId(@PathVariable Integer carritoId) {
        List<Carrito_AnadidoDTO> items = carritoAnadidoService.findCarritoAnadidosByCarritoId(carritoId);
        if (items.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(items);
    }

    @GetMapping("/carrito/{carritoId}/producto/{productoId}")
    public ResponseEntity<Carrito_AnadidoDTO> getItemInCarrito(
            @PathVariable Integer carritoId,
            @PathVariable Integer productoId) {
        Optional<Carrito_AnadidoDTO> item = carritoAnadidoService.findItemInCarrito(carritoId, productoId);
        return item.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Object> addItemToCarrito(@RequestBody Carrito_AnadidoDTO carritoAnadidoDTO) {
        try {
            Carrito_AnadidoDTO savedItem = carritoAnadidoService.addItemToCarrito(carritoAnadidoDTO);
            return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al añadir/actualizar producto en carrito: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateItemInCarrito(@PathVariable Integer id, @RequestBody Carrito_AnadidoDTO carritoAnadidoDTO) {
        try {
            Carrito_AnadidoDTO updatedItem = carritoAnadidoService.updateItemInCarrito(id, carritoAnadidoDTO);
            return ResponseEntity.ok(updatedItem);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItemFromCarrito(@PathVariable Integer id) {
        try {
            carritoAnadidoService.deleteItemFromCarrito(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/clear/{carritoId}")
    public ResponseEntity<Void> clearCarrito(@PathVariable Integer carritoId) {
        try {
            carritoAnadidoService.clearCarrito(carritoId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}