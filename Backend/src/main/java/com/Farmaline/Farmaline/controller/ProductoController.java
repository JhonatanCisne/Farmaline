package com.Farmaline.Farmaline.controller;

import java.math.BigDecimal; 
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Farmaline.Farmaline.dto.ProductoDTO;
import com.Farmaline.Farmaline.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> getAllProductos() {
        List<ProductoDTO> productos = productoService.findAllProductos();
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable Integer id) {
        Optional<ProductoDTO> producto = productoService.findProductoById(id);
        return producto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar/nombre")
    public ResponseEntity<List<ProductoDTO>> getProductosByNombre(@RequestParam String nombre) {
        List<ProductoDTO> productos = productoService.findProductosByNombre(nombre);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/buscar/precio-max")
    public ResponseEntity<List<ProductoDTO>> getProductosByPrecioLessThanEqual(@RequestParam BigDecimal precio) {
        List<ProductoDTO> productos = productoService.findProductosByPrecioLessThanEqual(precio);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<Integer> getStockDisponible(@PathVariable Integer id) {
        try {
            Integer stock = productoService.getStockDisponible(id);
            return ResponseEntity.ok(stock);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Object> createProducto(
            @RequestPart("producto") ProductoDTO productoDTO,
            @RequestPart("file") MultipartFile file) {
        try {
            ProductoDTO createdProducto = productoService.createProducto(productoDTO, file);
            return new ResponseEntity<>(createdProducto, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al crear el producto: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProducto(@PathVariable Integer id, @RequestBody ProductoDTO productoDTO) {
        try {
            ProductoDTO updatedProducto = productoService.updateProducto(id, productoDTO);
            return ResponseEntity.ok(updatedProducto);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Object> updateStock(@PathVariable Integer id, @RequestParam Integer cantidadCambio) {
        try {
            ProductoDTO updatedProducto = productoService.updateStock(id, cantidadCambio);
            return ResponseEntity.ok(updatedProducto);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Integer id) {
        try {
            productoService.deleteProducto(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar/descripcion")
    public ResponseEntity<List<ProductoDTO>> getProductosByDescripcion(@RequestParam String descripcion) {
        List<ProductoDTO> productos = productoService.findProductosByDescripcion(descripcion);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/buscar/stock-mayor-que")
    public ResponseEntity<List<ProductoDTO>> getProductosByStockGreaterThan(@RequestParam Integer stock) {
        List<ProductoDTO> productos = productoService.findProductosByStockGreaterThan(stock);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/buscar/precio-entre")
    public ResponseEntity<List<ProductoDTO>> getProductosByPrecioBetween(@RequestParam BigDecimal minPrecio, @RequestParam BigDecimal maxPrecio) {
        List<ProductoDTO> productos = productoService.findProductosByPrecioBetween(minPrecio, maxPrecio);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/buscar/nombre-y-descripcion")
    public ResponseEntity<List<ProductoDTO>> getProductosByNombreAndDescripcion(@RequestParam String nombreTerm, @RequestParam String descripcionTerm) {
        List<ProductoDTO> productos = productoService.findProductosByNombreAndDescripcion(nombreTerm, descripcionTerm);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/buscar/nombre-exacto")
    public ResponseEntity<ProductoDTO> getProductoByNombreExacto(@RequestParam String nombre) {
        Optional<ProductoDTO> producto = productoService.findProductoByNombreExacto(nombre);
        return producto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/contar/stock-bajo")
    public ResponseEntity<Long> countProductosWithLowStock(@RequestParam Integer stock) {
        long count = productoService.countProductosWithLowStock(stock);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/buscar/stock-menor-igual")
    public ResponseEntity<List<ProductoDTO>> getProductosByStockLessThanEqual(@RequestParam Integer stock) {
        List<ProductoDTO> productos = productoService.findProductosByStockLessThanEqual(stock);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/buscar/fecha-caducidad-antes")
    public ResponseEntity<List<ProductoDTO>> getProductosByFechaCaducidadBefore(@RequestParam String fecha) {
        List<ProductoDTO> productos = productoService.findProductosByFechaCaducidadBefore(LocalDate.parse(fecha));
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/buscar/fecha-ingreso-despues")
    public ResponseEntity<List<ProductoDTO>> getProductosByFechaIngresoAfter(@RequestParam String fecha) {
        List<ProductoDTO> productos = productoService.findProductosByFechaIngresoAfter(LocalDate.parse(fecha));
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/buscar/igv-entre")
    public ResponseEntity<List<ProductoDTO>> getProductosByIgvBetween(@RequestParam BigDecimal minIgv, @RequestParam BigDecimal maxIgv) {
        List<ProductoDTO> productos = productoService.findProductosByIgvBetween(minIgv, maxIgv);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/buscar/precio-final-entre")
    public ResponseEntity<List<ProductoDTO>> getProductosByPrecioFinalBetween(@RequestParam BigDecimal minPrecioFinal, @RequestParam BigDecimal maxPrecioFinal) {
        List<ProductoDTO> productos = productoService.findProductosByPrecioFinalBetween(minPrecioFinal, maxPrecioFinal);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/contar/precio-entre")
    public ResponseEntity<Long> countProductosByPrecioBetween(@RequestParam BigDecimal minPrecio, @RequestParam BigDecimal maxPrecio) {
        long count = productoService.countProductosByPrecioBetween(minPrecio, maxPrecio);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/ordenar/nombre-asc")
    public ResponseEntity<List<ProductoDTO>> getAllProductosOrderByNombreAsc() {
        List<ProductoDTO> productos = productoService.findAllProductosOrderByNombreAsc();
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/ordenar/precio-desc")
    public ResponseEntity<List<ProductoDTO>> getAllProductosOrderByPrecioDesc() {
        List<ProductoDTO> productos = productoService.findAllProductosOrderByPrecioDesc();
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }
}