package com.farmaline.farmaline.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.dto.ProductoDTO;
import com.farmaline.farmaline.model.Producto;
import com.farmaline.farmaline.repository.CalificacionRepository;
import com.farmaline.farmaline.repository.CarritoAnadidoRepository; 
import com.farmaline.farmaline.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CalificacionRepository calificacionRepository;
    @Autowired
    private CarritoAnadidoRepository carritoAnadidoRepository;
    @Autowired
    private LoteProductoService loteProductoService; 

    public List<ProductoDTO> getAllProductos() {
        return productoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProductoDTO> getProductoById(Integer id) {
        return productoRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional
    public ProductoDTO createProducto(ProductoDTO productoDTO) {
        if (productoRepository.existsByNombreIgnoreCase(productoDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un producto con el mismo nombre.");
        }

        Producto producto = convertToEntity(productoDTO);
        if (producto.getPrecio() != null && producto.getIgv() != null) {
            BigDecimal igvMonto = producto.getPrecio().multiply(producto.getIgv());
            producto.setPrecioFinal(producto.getPrecio().add(igvMonto));
        } else {
            throw new IllegalArgumentException("Precio e IGV son requeridos para calcular el Precio Final.");
        }

        Producto savedProducto = productoRepository.save(producto);
        return convertToDTO(savedProducto);
    }

    @Transactional
    public Optional<ProductoDTO> updateProducto(Integer id, ProductoDTO productoDTO) {
        return productoRepository.findById(id).map(existingProducto -> {
            if (!existingProducto.getNombre().equalsIgnoreCase(productoDTO.getNombre()) &&
                productoRepository.existsByNombreIgnoreCase(productoDTO.getNombre())) {
                throw new IllegalArgumentException("Ya existe otro producto con el nuevo nombre.");
            }

            existingProducto.setNombre(productoDTO.getNombre());
            existingProducto.setDescripcion(productoDTO.getDescripcion());
            existingProducto.setPrecio(productoDTO.getPrecio());
            existingProducto.setImagen(productoDTO.getImagen());
            existingProducto.setIgv(productoDTO.getIgv());
            existingProducto.setLaboratorio(productoDTO.getLaboratorio());

            if (existingProducto.getPrecio() != null && existingProducto.getIgv() != null) {
                BigDecimal igvMonto = existingProducto.getPrecio().multiply(existingProducto.getIgv());
                existingProducto.setPrecioFinal(existingProducto.getPrecio().add(igvMonto));
            } else {
                throw new IllegalArgumentException("Precio e IGV son requeridos para recalcular el Precio Final.");
            }

            Producto updatedProducto = productoRepository.save(existingProducto);
            return convertToDTO(updatedProducto);
        });
    }

    @Transactional
    public boolean deleteProducto(Integer id) {
        if (productoRepository.existsById(id)) {
            calificacionRepository.deleteByProducto_IdProducto(id);
            carritoAnadidoRepository.deleteByProducto_IdProducto(id);
            loteProductoService.deleteByProducto_IdProducto(id);

            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private ProductoDTO convertToDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(producto.getIdProducto());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setImagen(producto.getImagen());
        dto.setIgv(producto.getIgv());
        dto.setPrecioFinal(producto.getPrecioFinal());
        dto.setLaboratorio(producto.getLaboratorio());
        return dto;
    }

    private Producto convertToEntity(ProductoDTO productoDTO) {
        Producto producto = new Producto();
        producto.setIdProducto(productoDTO.getIdProducto());
        producto.setNombre(productoDTO.getNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setImagen(productoDTO.getImagen());
        producto.setIgv(productoDTO.getIgv());
        producto.setPrecioFinal(productoDTO.getPrecioFinal()); 
        producto.setLaboratorio(productoDTO.getLaboratorio());
        return producto;
    }
}