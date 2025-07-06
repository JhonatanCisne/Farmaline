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

import com.farmaline.farmaline.dto.DetallePedidoDTO;
import com.farmaline.farmaline.service.DetallePedidoService;

@RestController
@RequestMapping("/api/detalles-pedido")
public class DetallePedidoController {

    @Autowired
    private DetallePedidoService detallePedidoService;

    @GetMapping
    public ResponseEntity<List<DetallePedidoDTO>> getAllDetallesPedido() {
        List<DetallePedidoDTO> detalles = detallePedidoService.getAllDetallesPedido();
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallePedidoDTO> getDetallePedidoById(@PathVariable Integer id) {
        return detallePedidoService.getDetallePedidoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<List<DetallePedidoDTO>> getDetallesByPedidoId(@PathVariable Integer idPedido) {
        List<DetallePedidoDTO> detalles = detallePedidoService.getDetallesByPedidoId(idPedido);
        return ResponseEntity.ok(detalles);
    }

    @PostMapping
    public ResponseEntity<DetallePedidoDTO> createDetallePedido(@RequestBody DetallePedidoDTO detallePedidoDTO) {
        try {
            DetallePedidoDTO createdDetalle = detallePedidoService.createDetallePedido(detallePedidoDTO);
            return new ResponseEntity<>(createdDetalle, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{idDetallePedido}")
    public ResponseEntity<DetallePedidoDTO> updateDetallePedido(
            @PathVariable Integer idDetallePedido,
            @RequestBody DetallePedidoDTO detallePedidoDTO) {
        try {
            return detallePedidoService.updateDetallePedido(idDetallePedido, detallePedidoDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{idDetallePedido}")
    public ResponseEntity<Void> deleteDetallePedido(@PathVariable Integer idDetallePedido) {
        if (detallePedidoService.deleteDetallePedido(idDetallePedido)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}