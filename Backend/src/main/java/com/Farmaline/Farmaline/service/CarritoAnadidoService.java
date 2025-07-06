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

    @Autowired
    private CarritoAnadidoRepository carritoAnadidoRepository;
    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private ProductoRepository productoRepository;

    public List<CarritoAnadidoDTO> getAllCarritoAnadidos() {
        return carritoAnadidoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<CarritoAnadidoDTO> getCarritoAnadidoById(Integer id) {
        return carritoAnadidoRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<CarritoAnadidoDTO> getCarritoAnadidosByCarritoId(Integer idCarrito) {
        return carritoAnadidoRepository.findByCarrito_IdCarrito(idCarrito).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CarritoAnadidoDTO addProductoToCarrito(CarritoAnadidoDTO carritoAnadidoDTO) {
        Carrito carrito = carritoRepository.findById(carritoAnadidoDTO.getIdCarrito())
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado con ID: " + carritoAnadidoDTO.getIdCarrito()));
        
        Producto producto = productoRepository.findById(carritoAnadidoDTO.getIdProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + carritoAnadidoDTO.getIdProducto()));

        if (carritoAnadidoDTO.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero para añadir al carrito.");
        }

        Optional<Carrito_Anadido> existingItem = carritoAnadidoRepository.findByCarrito_IdCarritoAndProducto_IdProducto(
            carritoAnadidoDTO.getIdCarrito(), carritoAnadidoDTO.getIdProducto());

        Carrito_Anadido carritoAnadido;

        if (existingItem.isPresent()) {
            carritoAnadido = existingItem.get();
            carritoAnadido.setCantidad(carritoAnadido.getCantidad() + carritoAnadidoDTO.getCantidad());
        } else {
            carritoAnadido = new Carrito_Anadido();
            carritoAnadido.setCarrito(carrito);
            carritoAnadido.setProducto(producto);
            carritoAnadido.setCantidad(carritoAnadidoDTO.getCantidad());
        }
        
        Carrito_Anadido savedItem = carritoAnadidoRepository.save(carritoAnadido);
        return convertToDTO(savedItem);
    }

    @Transactional
    public Optional<CarritoAnadidoDTO> updateCantidadInCarrito(Integer idCarritoAnadido, int newCantidad) {
        Optional<Carrito_Anadido> itemOptional = carritoAnadidoRepository.findById(idCarritoAnadido);

        if (itemOptional.isPresent()) {
            Carrito_Anadido existingItem = itemOptional.get();

            if (newCantidad <= 0) {
                carritoAnadidoRepository.delete(existingItem);
                return Optional.empty();
            } else {
                existingItem.setCantidad(newCantidad);
                Carrito_Anadido updatedItem = carritoAnadidoRepository.save(existingItem);
                return Optional.of(convertToDTO(updatedItem));
            }
        }
        return Optional.empty();
    }
    
    @Transactional
    public boolean removeProductoFromCarrito(Integer idCarrito, Integer idProducto) {
        Optional<Carrito_Anadido> itemToRemove = carritoAnadidoRepository.findByCarrito_IdCarritoAndProducto_IdProducto(idCarrito, idProducto);
        if (itemToRemove.isPresent()) {
            carritoAnadidoRepository.delete(itemToRemove.get());
            return true;
        }
        return false;
    }

    @Transactional
    public void clearCarrito(Integer idCarrito) {
        carritoAnadidoRepository.deleteByCarrito_IdCarrito(idCarrito);
    }

    private CarritoAnadidoDTO convertToDTO(Carrito_Anadido carritoAnadido) {
        CarritoAnadidoDTO dto = new CarritoAnadidoDTO();
        dto.setIdCarritoAnadido(carritoAnadido.getIdCarritoAnadido());
        dto.setCantidad(carritoAnadido.getCantidad());
        dto.setIdProducto(carritoAnadido.getProducto() != null ? carritoAnadido.getProducto().getIdProducto() : null);
        dto.setIdCarrito(carritoAnadido.getCarrito() != null ? carritoAnadido.getCarrito().getIdCarrito() : null);
        return dto;
    }

}