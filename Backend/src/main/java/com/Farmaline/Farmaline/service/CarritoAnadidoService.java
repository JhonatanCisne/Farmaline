package com.farmaline.farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.dto.CarritoAnadidoDTO;
import com.farmaline.farmaline.model.Carrito;
import com.farmaline.farmaline.model.Carrito_Anadido;
import com.farmaline.farmaline.model.Producto;
import com.farmaline.farmaline.repository.CarritoAnadidoRepository;
import com.farmaline.farmaline.repository.CarritoRepository;
import com.farmaline.farmaline.repository.ProductoRepository;

@Service
public class CarritoAnadidoService {

    private final CarritoAnadidoRepository carritoAnadidoRepository;
    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public CarritoAnadidoService(CarritoAnadidoRepository carritoAnadidoRepository, CarritoRepository carritoRepository, ProductoRepository productoRepository) {
        this.carritoAnadidoRepository = carritoAnadidoRepository;
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
    }

    public List<CarritoAnadidoDTO> obtenerTodosCarritoAnadido() {
        return carritoAnadidoRepository.findAll().stream()
                .map(this::convertirACarritoAnadidoDTO)
                .collect(Collectors.toList());
    }

    public Optional<CarritoAnadidoDTO> obtenerCarritoAnadidoPorId(Integer id) {
        return carritoAnadidoRepository.findById(id)
                .map(this::convertirACarritoAnadidoDTO);
    }

    public List<CarritoAnadidoDTO> obtenerItemsCarritoPorIdCarrito(Integer idCarrito) {
        return carritoAnadidoRepository.findByCarritoIdCarrito(idCarrito).stream()
                .map(this::convertirACarritoAnadidoDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CarritoAnadidoDTO crearCarritoAnadido(CarritoAnadidoDTO carritoAnadidoDTO) {
        Carrito_Anadido carritoAnadido = new Carrito_Anadido();
        carritoAnadido.setCantidad(carritoAnadidoDTO.getCantidad());

        Producto producto = productoRepository.findById(carritoAnadidoDTO.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        carritoAnadido.setProducto(producto);

        Carrito carrito = carritoRepository.findById(carritoAnadidoDTO.getIdCarrito())
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        carritoAnadido.setCarrito(carrito);

        carritoAnadido = carritoAnadidoRepository.save(carritoAnadido);
        return convertirACarritoAnadidoDTO(carritoAnadido);
    }

    @Transactional
    public Optional<CarritoAnadidoDTO> actualizarCarritoAnadido(Integer id, CarritoAnadidoDTO carritoAnadidoDTO) {
        return carritoAnadidoRepository.findById(id)
                .map(carritoAnadidoExistente -> {
                    carritoAnadidoExistente.setCantidad(carritoAnadidoDTO.getCantidad());

                    if (carritoAnadidoDTO.getIdProducto() != null) {
                        Producto producto = productoRepository.findById(carritoAnadidoDTO.getIdProducto())
                                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                        carritoAnadidoExistente.setProducto(producto);
                    }

                    if (carritoAnadidoDTO.getIdCarrito() != null) {
                        Carrito carrito = carritoRepository.findById(carritoAnadidoDTO.getIdCarrito())
                                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
                        carritoAnadidoExistente.setCarrito(carrito);
                    }
                    return convertirACarritoAnadidoDTO(carritoAnadidoRepository.save(carritoAnadidoExistente));
                });
    }

    public boolean eliminarCarritoAnadido(Integer id) {
        if (carritoAnadidoRepository.existsById(id)) {
            carritoAnadidoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public void limpiarItemsDeCarrito(Integer idCarrito) {
        carritoAnadidoRepository.deleteByCarritoIdCarrito(idCarrito);
    }

    private CarritoAnadidoDTO convertirACarritoAnadidoDTO(Carrito_Anadido carritoAnadido) {
        CarritoAnadidoDTO dto = new CarritoAnadidoDTO();
        dto.setIdCarritoAnadido(carritoAnadido.getIdCarritoAnadido());
        dto.setCantidad(carritoAnadido.getCantidad());
        if (carritoAnadido.getProducto() != null) {
            dto.setIdProducto(carritoAnadido.getProducto().getIdProducto());
            dto.setNombreProducto(carritoAnadido.getProducto().getNombre());
        }
        if (carritoAnadido.getCarrito() != null) {
            dto.setIdCarrito(carritoAnadido.getCarrito().getIdCarrito());
        }
        return dto;
    }
}