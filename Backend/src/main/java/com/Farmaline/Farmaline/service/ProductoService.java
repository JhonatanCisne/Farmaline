package com.Farmaline.Farmaline.service;

import java.math.BigDecimal; // Importar BigDecimal
import java.time.LocalDate;
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
        dto.setStockDisponible(producto.getStockDisponible());
        dto.setPrecio(producto.getPrecio()); // BigDecimal a BigDecimal
        dto.setImagen(producto.getImagen());
        dto.setFechaCaducidad(producto.getFechaCaducidad());
        dto.setFechaIngreso(producto.getFechaIngreso());
        dto.setIgv(producto.getIgv());       // BigDecimal a BigDecimal
        dto.setPrecioFinal(producto.getPrecioFinal()); // BigDecimal a BigDecimal
        return dto;
    }

    private Producto convertToEntity(ProductoDTO dto) {
        if (dto == null) {
            return null;
        }
        Producto producto = new Producto();
        // Cuando creamos una nueva entidad, el ID lo genera la base de datos
        // producto.setIdProducto(dto.getIdProducto()); // Esto no es necesario para una nueva entidad
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setStockDisponible(Optional.ofNullable(dto.getStockDisponible()).orElse(0)); // Valor predeterminado a 0 si es nulo
        producto.setPrecio(dto.getPrecio()); // BigDecimal a BigDecimal
        producto.setImagen(dto.getImagen());
        producto.setFechaCaducidad(dto.getFechaCaducidad());
        producto.setFechaIngreso(Optional.ofNullable(dto.getFechaIngreso()).orElse(LocalDate.now())); // Valor predeterminado a la fecha actual si es nulo
        producto.setIgv(dto.getIgv());       // BigDecimal a BigDecimal
        producto.setPrecioFinal(dto.getPrecioFinal()); // BigDecimal a BigDecimal
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

    // --- CAMBIOS EN MÉTODOS DE BÚSQUEDA Y CREACIÓN ---

    @Transactional(readOnly = true)
    public List<ProductoDTO> findProductosByPrecioLessThanEqual(BigDecimal precio) { // Cambiado a BigDecimal
        return productoRepository.findByPrecioLessThanEqual(precio).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductoDTO createProducto(ProductoDTO productoDTO) {
        if (productoDTO.getNombre() == null || productoDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        // Usar compareTo para comparar BigDecimals
        if (productoDTO.getPrecio() == null || productoDTO.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio del producto debe ser mayor que cero.");
        }
        if (productoDTO.getFechaIngreso() == null) {
            productoDTO.setFechaIngreso(LocalDate.now());
        }
        if (productoDTO.getIgv() == null || productoDTO.getIgv().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El IGV no puede ser negativo.");
        }
        if (productoDTO.getPrecioFinal() == null || productoDTO.getPrecioFinal().compareTo(BigDecimal.ZERO) <= 0) {
             throw new IllegalArgumentException("El precio final del producto debe ser mayor que cero.");
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
                if (productoDTO.getStockDisponible() != null && productoDTO.getStockDisponible() >= 0) {
                    producto.setStockDisponible(productoDTO.getStockDisponible());
                }
                // Usar compareTo para comparar BigDecimals
                if (productoDTO.getPrecio() != null && productoDTO.getPrecio().compareTo(BigDecimal.ZERO) > 0) {
                    producto.setPrecio(productoDTO.getPrecio());
                }
                if (productoDTO.getImagen() != null) {
                    producto.setImagen(productoDTO.getImagen());
                }
                if (productoDTO.getFechaCaducidad() != null) {
                    producto.setFechaCaducidad(productoDTO.getFechaCaducidad());
                }
                // Usar compareTo para comparar BigDecimals
                if (productoDTO.getIgv() != null && productoDTO.getIgv().compareTo(BigDecimal.ZERO) >= 0) {
                    producto.setIgv(productoDTO.getIgv());
                }
                // Usar compareTo para comparar BigDecimals
                if (productoDTO.getPrecioFinal() != null && productoDTO.getPrecioFinal().compareTo(BigDecimal.ZERO) > 0) {
                    producto.setPrecioFinal(productoDTO.getPrecioFinal());
                }
                Producto updatedProducto = productoRepository.save(producto);
                return convertToDto(updatedProducto);
            }).orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    // El resto de los métodos se mantienen, pero asegúrate de que los métodos en ProductoRepository
    // que interactúan con Precio, IGV o Precio_Final también usen BigDecimal en sus firmas.

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

    @Transactional(readOnly = true)
    public List<ProductoDTO> findProductosByDescripcion(String descripcion) {
        return productoRepository.findByDescripcionContainingIgnoreCase(descripcion).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findProductosByStockGreaterThan(Integer stock) {
        return productoRepository.findByStockDisponibleGreaterThan(stock).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findProductosByPrecioBetween(BigDecimal minPrecio, BigDecimal maxPrecio) { // Cambiado a BigDecimal
        return productoRepository.findByPrecioBetween(minPrecio, maxPrecio).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findProductosByNombreAndDescripcion(String nombreTerm, String descripcionTerm) {
        return productoRepository.findByNombreContainingIgnoreCaseAndDescripcionContainingIgnoreCase(nombreTerm, descripcionTerm).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProductoDTO> findProductoByNombreExacto(String nombre) {
        return productoRepository.findByNombreIgnoreCase(nombre)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public long countProductosWithLowStock(Integer stock) {
        return productoRepository.countByStockDisponibleLessThan(stock);
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findProductosByStockLessThanEqual(Integer stock) {
        return productoRepository.findByStockDisponibleLessThanEqual(stock).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findProductosByFechaCaducidadBefore(LocalDate fecha) {
        return productoRepository.findByFechaCaducidadBefore(fecha).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findProductosByFechaIngresoAfter(LocalDate fecha) {
        return productoRepository.findByFechaIngresoAfter(fecha).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findProductosByIgvBetween(BigDecimal minIgv, BigDecimal maxIgv) { // Cambiado a BigDecimal
        return productoRepository.findByIgvBetween(minIgv, maxIgv).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findProductosByPrecioFinalBetween(BigDecimal minPrecioFinal, BigDecimal maxPrecioFinal) { // Cambiado a BigDecimal
        return productoRepository.findByPrecioFinalBetween(minPrecioFinal, maxPrecioFinal).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countProductosByPrecioBetween(BigDecimal minPrecio, BigDecimal maxPrecio) { // Cambiado a BigDecimal
        return productoRepository.countByPrecioBetween(minPrecio, maxPrecio);
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findAllProductosOrderByNombreAsc() {
        return productoRepository.findAllByOrderByNombreAsc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findAllProductosOrderByPrecioDesc() {
        return productoRepository.findAllByOrderByPrecioDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}