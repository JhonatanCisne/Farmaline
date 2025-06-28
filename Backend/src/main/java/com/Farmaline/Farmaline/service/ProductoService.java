package com.farmaline.farmaline.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.dto.ProductoDTO;
import com.farmaline.farmaline.model.Producto;
import com.farmaline.farmaline.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<ProductoDTO> obtenerTodosProductos() {
        return productoRepository.findAll().stream()
                .map(this::convertirAProductoDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProductoDTO> obtenerProductoPorId(Integer id) {
        return productoRepository.findById(id)
                .map(this::convertirAProductoDTO);
    }

    public ProductoDTO crearProducto(ProductoDTO productoDTO) {
        Producto producto = convertirAProductoEntidad(productoDTO);
        producto = productoRepository.save(producto);
        return convertirAProductoDTO(producto);
    }

    @Transactional
    public Optional<ProductoDTO> actualizarProducto(Integer id, ProductoDTO productoDTO) {
        return productoRepository.findById(id)
                .map(productoExistente -> {
                    productoExistente.setNombre(productoDTO.getNombre());
                    productoExistente.setDescripcion(productoDTO.getDescripcion());
                    productoExistente.setStockDisponible(productoDTO.getStockDisponible());
                    productoExistente.setPrecio(productoDTO.getPrecio());
                    productoExistente.setImagen(productoDTO.getImagen());
                    productoExistente.setFechaCaducidad(productoDTO.getFechaCaducidad());
                    productoExistente.setFechaIngreso(productoDTO.getFechaIngreso());
                    productoExistente.setIgv(productoDTO.getIgv());
                    productoExistente.setPrecioFinal(productoDTO.getPrecioFinal());
                    return convertirAProductoDTO(productoRepository.save(productoExistente));
                });
    }

    public boolean eliminarProducto(Integer id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public Optional<ProductoDTO> actualizarStock(Integer id, Integer cantidadCambio) {
        return productoRepository.findById(id)
                .map(productoExistente -> {
                    if (productoExistente.getStockDisponible() + cantidadCambio < 0) {
                        throw new RuntimeException("No hay suficiente stock para esta operación.");
                    }
                    productoExistente.setStockDisponible(productoExistente.getStockDisponible() + cantidadCambio);
                    return convertirAProductoDTO(productoRepository.save(productoExistente));
                });
    }

    private ProductoDTO convertirAProductoDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(producto.getIdProducto());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setStockDisponible(producto.getStockDisponible());
        dto.setPrecio(producto.getPrecio());
        dto.setImagen(producto.getImagen());
        dto.setFechaCaducidad(producto.getFechaCaducidad());
        dto.setFechaIngreso(producto.getFechaIngreso());
        dto.setIgv(producto.getIgv());
        dto.setPrecioFinal(producto.getPrecioFinal());
        return dto;
    }

    private Producto convertirAProductoEntidad(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setIdProducto(dto.getIdProducto());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setStockDisponible(dto.getStockDisponible());
        producto.setPrecio(dto.getPrecio());
        producto.setImagen(dto.getImagen());
        producto.setFechaCaducidad(dto.getFechaCaducidad());
        producto.setFechaIngreso(dto.getFechaIngreso());
        producto.setIgv(dto.getIgv());
        producto.setPrecioFinal(dto.getPrecioFinal());
        return producto;
    }

    public List<ProductoDTO> obtenerProductosConFiltros(String nombre, Integer stockMinimo, Integer stockMaximo, LocalDate fechaCaducidadHasta) {
        List<Producto> productos = productoRepository.findProductosByFilters(nombre, stockMinimo, stockMaximo, fechaCaducidadHasta);
        return productos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ProductoDTO convertToDto(Producto producto) {
        return new ProductoDTO(
            producto.getIdProducto(),
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getStockDisponible(),
            producto.getPrecio(),
            producto.getImagen(),
            producto.getFechaCaducidad(),
            producto.getFechaIngreso(),
            producto.getIgv(),
            producto.getPrecioFinal()
        );
    }
}