package com.Farmaline.farmaline.controller;

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
@RequestMapping("/api/carrito-anadido")
public class CarritoAnadidoController {

    private final CarritoAnadidoService carritoAnadidoService;

    @Autowired
    public CarritoAnadidoController(CarritoAnadidoService carritoAnadidoService) {
        this.carritoAnadidoService = carritoAnadidoService;
    }

    @GetMapping
    public ResponseEntity<List<CarritoAnadidoDTO>> obtenerTodosCarritoAnadido() {
        List<CarritoAnadidoDTO> carritoAnadido = carritoAnadidoService.obtenerTodosCarritoAnadido();
        return ResponseEntity.ok(carritoAnadido);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarritoAnadidoDTO> obtenerCarritoAnadidoPorId(@PathVariable Integer id) {
        return carritoAnadidoService.obtenerCarritoAnadidoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/carrito/{idCarrito}")
    public ResponseEntity<List<CarritoAnadidoDTO>> obtenerItemsCarritoPorIdCarrito(@PathVariable Integer idCarrito) {
        List<CarritoAnadidoDTO> items = carritoAnadidoService.obtenerItemsCarritoPorIdCarrito(idCarrito);
        return ResponseEntity.ok(items);
    }

    @PostMapping
    public ResponseEntity<CarritoAnadidoDTO> crearCarritoAnadido(@RequestBody CarritoAnadidoDTO carritoAnadidoDTO) {
        try {
            CarritoAnadidoDTO nuevoCarritoAnadido = carritoAnadidoService.crearCarritoAnadido(carritoAnadidoDTO);
            return new ResponseEntity<>(nuevoCarritoAnadido, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarritoAnadidoDTO> actualizarCarritoAnadido(@PathVariable Integer id, @RequestBody CarritoAnadidoDTO carritoAnadidoDTO) {
        try {
            return carritoAnadidoService.actualizarCarritoAnadido(id, carritoAnadidoDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCarritoAnadido(@PathVariable Integer id) {
        if (carritoAnadidoService.eliminarCarritoAnadido(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}