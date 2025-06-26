package com.farmaline.farmaline.controller;

import java.util.List;
import java.util.Map;

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

    private final PedidoService pedidoService;

    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> obtenerTodosPedidos() {
        List<PedidoDTO> pedidos = pedidoService.obtenerTodosPedidos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> obtenerPedidoPorId(@PathVariable Integer id) {
        return pedidoService.obtenerPedidoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/crear-desde-carrito")
    public ResponseEntity<PedidoDTO> crearPedidoDesdeCarrito(@RequestBody Map<String, Integer> payload) {
        Integer idUsuario = payload.get("idUsuario");
        if (idUsuario == null) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            PedidoDTO nuevoPedido = pedidoService.crearPedidoDesdeCarrito(idUsuario);
            return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoDTO> actualizarPedido(@PathVariable Integer id, @RequestBody PedidoDTO pedidoDTO) {
        try {
            return pedidoService.actualizarPedido(id, pedidoDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Integer id) {
        if (pedidoService.eliminarPedido(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}