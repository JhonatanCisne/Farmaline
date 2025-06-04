package com.Farmaline.Farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Farmaline.Farmaline.dto.ProductoDTO;
import com.Farmaline.Farmaline.model.Producto;
import com.Farmaline.Farmaline.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    private ProductoDTO convertToDto(Producto producto) {
        if (producto == null) {
            return null;
        }
        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(producto.getIdProducto());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setImagen(producto.getImagen());
        return dto;
    }

    private Producto convertToEntity(ProductoDTO dto) {
        if (dto == null) {
            return null;
        }
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setImagen(dto.getImagen());
        producto.setStockDisponible(0);
        return producto;
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findAllProductos() {
        return productoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProductoDTO> findProductoById(Integer id) {
        return productoRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findProductosByNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findProductosByPrecioLessThanEqual(float precio) {
        return productoRepository.findByPrecioLessThanEqual(precio).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductoDTO createProducto(ProductoDTO productoDTO) {
        if (productoDTO.getNombre() == null || productoDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (productoDTO.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio del producto debe ser mayor que cero.");
        }

        if (productoRepository.findByNombreIgnoreCase(productoDTO.getNombre()).isPresent()) {
            throw new IllegalStateException("Ya existe un producto con el nombre: " + productoDTO.getNombre());
        }

        Producto producto = convertToEntity(productoDTO);
        Producto savedProducto = productoRepository.save(producto);
        return convertToDto(savedProducto);
    }

    @Transactional
    public ProductoDTO updateProducto(Integer id, ProductoDTO productoDTO) {
        return productoRepository.findById(id)
            .map(producto -> {
                if (productoDTO.getNombre() != null) {
                    producto.setNombre(productoDTO.getNombre());
                }
                if (productoDTO.getDescripcion() != null) {
                    producto.setDescripcion(productoDTO.getDescripcion());
                }
                if (productoDTO.getPrecio() > 0) {
                    producto.setPrecio(productoDTO.getPrecio());
                }
                if (productoDTO.getImagen() != null) {
                    producto.setImagen(productoDTO.getImagen());
                }
                Producto updatedProducto = productoRepository.save(producto);
                return convertToDto(updatedProducto);
            }).orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @Transactional
    public void deleteProducto(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto con ID " + id + " no encontrado para eliminar.");
        }
        productoRepository.deleteById(id);
    }

    @Transactional
    public ProductoDTO updateStock(Integer id, Integer cantidadCambio) {
        return productoRepository.findById(id)
            .map(producto -> {
                int nuevoStock = producto.getStockDisponible() + cantidadCambio;
                if (nuevoStock < 0) {
                    throw new IllegalArgumentException("No hay suficiente stock para esta operación. Stock actual: " + producto.getStockDisponible());
                }
                producto.setStockDisponible(nuevoStock);
                Producto updatedProducto = productoRepository.save(producto);
                return convertToDto(updatedProducto);
            }).orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Integer getStockDisponible(Integer id) {
        return productoRepository.findById(id)
            .map(Producto::getStockDisponible)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }
}