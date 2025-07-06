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

import com.farmaline.farmaline.dto.LoteProductoDTO;
import com.farmaline.farmaline.service.LoteProductoService;

@RestController
@RequestMapping("/api/lotes-producto")
public class LoteProductoController {

    @Autowired
    private LoteProductoService loteProductoService;

    @GetMapping
    public ResponseEntity<List<LoteProductoDTO>> getAllLotesProducto() {
        List<LoteProductoDTO> lotes = loteProductoService.getAllLotesProducto();
        return ResponseEntity.ok(lotes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoteProductoDTO> getLoteProductoById(@PathVariable Integer id) {
        return loteProductoService.getLoteProductoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<LoteProductoDTO>> getLotesByProductId(@PathVariable Integer idProducto) {
        List<LoteProductoDTO> lotes = loteProductoService.getLotesByProductId(idProducto);
        return ResponseEntity.ok(lotes);
    }

    @PostMapping
    public ResponseEntity<LoteProductoDTO> createLoteProducto(@RequestBody LoteProductoDTO loteProductoDTO) {
        try {
            LoteProductoDTO createdLote = loteProductoService.createLoteProducto(loteProductoDTO);
            return new ResponseEntity<>(createdLote, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{idLoteProducto}")
    public ResponseEntity<LoteProductoDTO> updateLoteProducto(@PathVariable Integer idLoteProducto, @RequestBody LoteProductoDTO loteProductoDTO) {
        try {
            return loteProductoService.updateLoteProducto(idLoteProducto, loteProductoDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoteProducto(@PathVariable Integer id) {
        if (loteProductoService.deleteLoteProducto(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{idLoteProducto}/increase-stock/{cantidad}")
    public ResponseEntity<Void> increaseStock(@PathVariable Integer idLoteProducto, @PathVariable int cantidad) {
        try {
            if (loteProductoService.increaseStock(idLoteProducto, cantidad)) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/total-stock/producto/{idProducto}")
    public ResponseEntity<Integer> getTotalStockByProductId(@PathVariable Integer idProducto) {
        int totalStock = loteProductoService.getTotalStockByProductId(idProducto);
        return ResponseEntity.ok(totalStock);
    }

    @DeleteMapping("/expired")
    public ResponseEntity<Integer> deleteExpiredLotes() {
        int deletedCount = loteProductoService.deleteExpiredLotes();
        return ResponseEntity.ok(deletedCount);
    }
}