package com.Farmaline.Farmaline.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.Farmaline.Farmaline.dto.ProductoDTO;
import com.Farmaline.Farmaline.model.Calificacion;
import com.Farmaline.Farmaline.model.Carrito_Anadido;
import com.Farmaline.Farmaline.model.Producto;
import com.Farmaline.Farmaline.repository.CalificacionRepository;
import com.Farmaline.Farmaline.repository.Carrito_AnadidoRepository;
import com.Farmaline.Farmaline.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final Carrito_AnadidoRepository carritoAnadidoRepository;
    private final CalificacionRepository calificacionRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public ProductoService(ProductoRepository productoRepository,
                           Carrito_AnadidoRepository carritoAnadidoRepository,
                           CalificacionRepository calificacionRepository) {
        this.productoRepository = productoRepository;
        this.carritoAnadidoRepository = carritoAnadidoRepository;
        this.calificacionRepository = calificacionRepository;
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
        dto.setPrecio(producto.getPrecio());
        dto.setImagen(producto.getImagen());
        dto.setFechaCaducidad(producto.getFechaCaducidad());
        dto.setFechaIngreso(producto.getFechaIngreso());
        dto.setIgv(producto.getIgv());
        dto.setPrecioFinal(producto.getPrecioFinal());
        return dto;
    }

    private Producto convertToEntity(ProductoDTO dto) {
        if (dto == null) {
            return null;
        }
        Producto producto = new Producto();
        if (dto.getIdProducto() != null) {
            producto.setIdProducto(dto.getIdProducto());
        }
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setStockDisponible(Optional.ofNullable(dto.getStockDisponible()).orElse(0));
        producto.setPrecio(dto.getPrecio());
        producto.setImagen(dto.getImagen());
        producto.setFechaCaducidad(dto.getFechaCaducidad());
        producto.setFechaIngreso(Optional.ofNullable(dto.getFechaIngreso()).orElse(LocalDate.now()));
        producto.setIgv(dto.getIgv());
        producto.setPrecioFinal(dto.getPrecioFinal());
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
    public List<ProductoDTO> findProductosByPrecioLessThanEqual(BigDecimal precio) {
        return productoRepository.findByPrecioLessThanEqual(precio).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductoDTO createProducto(ProductoDTO productoDTO, MultipartFile file) throws IOException {
        if (productoDTO.getNombre() == null || productoDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
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

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        productoDTO.setImagen("/" + uploadDir + "/" + fileName);

        Producto producto = convertToEntity(productoDTO);
        Producto savedProducto = productoRepository.save(producto);
        return convertToDto(savedProducto);
    }

    @Transactional
    public ProductoDTO updateProducto(Integer id, ProductoDTO productoDTO) {
        return productoRepository.findById(id)
            .map(productoExistente -> {
                if (productoDTO.getNombre() != null) {
                    productoExistente.setNombre(productoDTO.getNombre());
                }
                if (productoDTO.getDescripcion() != null) {
                    productoExistente.setDescripcion(productoDTO.getDescripcion());
                }
                if (productoDTO.getStockDisponible() != null && productoDTO.getStockDisponible() >= 0) {
                    productoExistente.setStockDisponible(productoDTO.getStockDisponible());
                }
                if (productoDTO.getPrecio() != null && productoDTO.getPrecio().compareTo(BigDecimal.ZERO) > 0) {
                    productoExistente.setPrecio(productoDTO.getPrecio());
                }
                if (productoDTO.getImagen() != null) {
                    productoExistente.setImagen(productoDTO.getImagen());
                }
                if (productoDTO.getFechaCaducidad() != null) {
                    productoExistente.setFechaCaducidad(productoDTO.getFechaCaducidad());
                }
                if (productoDTO.getFechaIngreso() != null) {
                    productoExistente.setFechaIngreso(productoDTO.getFechaIngreso());
                }
                if (productoDTO.getIgv() != null && productoDTO.getIgv().compareTo(BigDecimal.ZERO) >= 0) {
                    productoExistente.setIgv(productoDTO.getIgv());
                }
                if (productoDTO.getPrecioFinal() != null && productoDTO.getPrecioFinal().compareTo(BigDecimal.ZERO) > 0) {
                    productoExistente.setPrecioFinal(productoDTO.getPrecioFinal());
                }
                Producto updatedProducto = productoRepository.save(productoExistente);
                return convertToDto(updatedProducto);
            }).orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @Transactional
    public void deleteProducto(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto con ID " + id + " no encontrado para eliminar.");
        }

        try {
            List<Carrito_Anadido> carritosRelacionados = carritoAnadidoRepository.findByProductoIdProducto(id);
            if (!carritosRelacionados.isEmpty()) {
                carritoAnadidoRepository.deleteAll(carritosRelacionados);
            }

            List<Calificacion> calificacionesRelacionadas = calificacionRepository.findByProductoIdProducto(id);
            if (!calificacionesRelacionadas.isEmpty()) {
                calificacionRepository.deleteAll(calificacionesRelacionadas);
            }

            productoRepository.deleteById(id);

        } catch (Exception e) {
            throw new RuntimeException("No se pudo eliminar el producto debido a dependencias existentes (carritos o calificaciones). Detalles: " + e.getMessage(), e);
        }
    }

    @Transactional
    public ProductoDTO updateStock(Integer id, Integer cantidadCambio) {
        return productoRepository.findById(id)
            .map(productoExistente -> {
                int nuevoStock = productoExistente.getStockDisponible() + cantidadCambio;
                if (nuevoStock < 0) {
                    throw new IllegalArgumentException("No hay suficiente stock para esta operación. Stock actual: " + productoExistente.getStockDisponible());
                }
                productoExistente.setStockDisponible(nuevoStock);
                Producto updatedProducto = productoRepository.save(productoExistente);
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
    public List<ProductoDTO> findProductosByPrecioBetween(BigDecimal minPrecio, BigDecimal maxPrecio) {
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
    public List<ProductoDTO> findProductosByIgvBetween(BigDecimal minIgv, BigDecimal maxIgv) {
        return productoRepository.findByIgvBetween(minIgv, maxIgv).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findProductosByPrecioFinalBetween(BigDecimal minPrecioFinal, BigDecimal maxPrecioFinal) {
        return productoRepository.findByPrecioFinalBetween(minPrecioFinal, maxPrecioFinal).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countProductosByPrecioBetween(BigDecimal minPrecio, BigDecimal maxPrecio) {
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