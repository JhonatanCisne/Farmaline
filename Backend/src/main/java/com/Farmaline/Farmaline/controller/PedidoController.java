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

import com.farmaline.farmaline.dto.PedidoDTO;
import com.farmaline.farmaline.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> getAllPedidos() {
        List<PedidoDTO> pedidos = pedidoService.getAllPedidos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<PedidoDTO>> getPedidosByUsuarioId(@PathVariable Integer idUsuario) {
        List<PedidoDTO> pedidos = pedidoService.getPedidosByUsuarioId(idUsuario);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/repartidor/{idRepartidor}")
    public ResponseEntity<List<PedidoDTO>> getPedidosByRepartidorId(@PathVariable Integer idRepartidor) {
        List<PedidoDTO> pedidos = pedidoService.getPedidosByRepartidorId(idRepartidor);
        if (pedidos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/no-asignados")
    public ResponseEntity<List<PedidoDTO>> getPedidosNoAsignados() {
        List<PedidoDTO> pedidos = pedidoService.getPedidosNoAsignados();
        if (pedidos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> getPedidoById(@PathVariable Integer id) {
        return pedidoService.getPedidoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/from-carrito/{idUsuario}")
    public ResponseEntity<PedidoDTO> createPedidoFromCarrito(@PathVariable Integer idUsuario) {
        try {
            PedidoDTO createdPedido = pedidoService.createPedidoFromCarrito(idUsuario);
            return new ResponseEntity<>(createdPedido, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{idPedido}")
    public ResponseEntity<PedidoDTO> updatePedido(@PathVariable Integer idPedido, @RequestBody PedidoDTO pedidoDTO) {
        try {
            return pedidoService.updatePedido(idPedido, pedidoDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{idPedido}/asignar-repartidor/{idRepartidor}")
    public ResponseEntity<PedidoDTO> assignRepartidorToPedido(
            @PathVariable Integer idPedido,
            @PathVariable Integer idRepartidor) {
        try {
            return pedidoService.assignRepartidorToPedido(idPedido, idRepartidor)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Integer id) {
        if (pedidoService.deletePedido(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{idPedido}/confirmar-entrega")
    public ResponseEntity<PedidoDTO> confirmarEntregaUsuario(@PathVariable Integer idPedido) {
        try {
            return pedidoService.confirmarEntregaUsuario(idPedido)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{idPedido}/confirmar-pago")
    public ResponseEntity<PedidoDTO> confirmarPagoRepartidor(@PathVariable Integer idPedido) {
        try {
            return pedidoService.confirmarPagoRepartidor(idPedido)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoDTO>> getPedidosByEstado(@PathVariable String estado) {
        List<PedidoDTO> pedidos = pedidoService.getPedidosByEstado(estado);
        if (pedidos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<PedidoDTO>> getPedidosPendientes() {
        List<PedidoDTO> pedidos = pedidoService.getPedidosPendientes();
        if (pedidos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/realizados")
    public ResponseEntity<List<PedidoDTO>> getPedidosRealizados() {
        List<PedidoDTO> pedidos = pedidoService.getPedidosRealizados();
        if (pedidos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pedidos);
    }
}