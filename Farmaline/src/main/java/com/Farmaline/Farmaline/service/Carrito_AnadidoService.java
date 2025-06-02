package com.Farmaline.Farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Farmaline.Farmaline.dto.Carrito_AnadidoDTO;
import com.Farmaline.Farmaline.model.Carrito;
import com.Farmaline.Farmaline.model.Carrito_Anadido;
import com.Farmaline.Farmaline.model.Producto;
import com.Farmaline.Farmaline.repository.CarritoRepository;
import com.Farmaline.Farmaline.repository.Carrito_AnadidoRepository;
import com.Farmaline.Farmaline.repository.ProductoRepository;

@Service
public class Carrito_AnadidoService {

    private final Carrito_AnadidoRepository carritoAnadidoRepository;
    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public Carrito_AnadidoService(
            Carrito_AnadidoRepository carritoAnadidoRepository,
            CarritoRepository carritoRepository,
            ProductoRepository productoRepository) {
        this.carritoAnadidoRepository = carritoAnadidoRepository;
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
    }


    private Carrito_AnadidoDTO convertToDto(Carrito_Anadido carritoAnadido) {
        if (carritoAnadido == null) {
            return null;
        }
        Carrito_AnadidoDTO dto = new Carrito_AnadidoDTO();
        dto.setIdCarritoAnadido(carritoAnadido.getIdCarritoAnadido());
        dto.setCantidad(carritoAnadido.getCantidad());
        if (carritoAnadido.getProducto() != null) {
            dto.setIdProducto(carritoAnadido.getProducto().getIdProducto());
        }
        if (carritoAnadido.getCarrito() != null) {
            dto.setIdCarrito(carritoAnadido.getCarrito().getIdCarrito());
        }
        return dto;
    }

    private Carrito_Anadido convertToEntity(Carrito_AnadidoDTO dto) {
        if (dto == null) {
            return null;
        }
        Carrito_Anadido carritoAnadido = new Carrito_Anadido();
        carritoAnadido.setCantidad(dto.getCantidad());

        if (dto.getIdProducto() != null) {
            Producto producto = productoRepository.findById(dto.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.getIdProducto()));
            carritoAnadido.setProducto(producto);
        } else {
            throw new IllegalArgumentException("ID de producto es obligatorio para Carrito_Anadido.");
        }

        if (dto.getIdCarrito() != null) {
            Carrito carrito = carritoRepository.findById(dto.getIdCarrito())
                    .orElseThrow(() -> new RuntimeException("Carrito no encontrado con ID: " + dto.getIdCarrito()));
            carritoAnadido.setCarrito(carrito);
        } else {
            throw new IllegalArgumentException("ID de carrito es obligatorio para Carrito_Anadido.");
        }
        return carritoAnadido;
    }

    @Transactional(readOnly = true)
    public List<Carrito_AnadidoDTO> findAllCarritoAnadidos() {
        return carritoAnadidoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Carrito_AnadidoDTO> findCarritoAnadidoById(Integer id) {
        return carritoAnadidoRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<Carrito_AnadidoDTO> findCarritoAnadidosByCarritoId(Integer carritoId) {
        return carritoAnadidoRepository.findByCarritoIdCarrito(carritoId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Carrito_AnadidoDTO> findItemInCarrito(Integer carritoId, Integer productoId) {
        return carritoAnadidoRepository.findByCarritoIdCarritoAndProductoIdProducto(carritoId, productoId)
                .map(this::convertToDto);
    }

    @Transactional
    public Carrito_AnadidoDTO addItemToCarrito(Carrito_AnadidoDTO carritoAnadidoDTO) {
        if (carritoAnadidoDTO.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        Optional<Carrito_Anadido> existingItem = carritoAnadidoRepository
                .findByCarritoIdCarritoAndProductoIdProducto(
                        carritoAnadidoDTO.getIdCarrito(), carritoAnadidoDTO.getIdProducto());

        if (existingItem.isPresent()) {
            Carrito_Anadido itemToUpdate = existingItem.get();
            itemToUpdate.setCantidad(itemToUpdate.getCantidad() + carritoAnadidoDTO.getCantidad());
            return convertToDto(carritoAnadidoRepository.save(itemToUpdate));
        } else {
            Carrito_Anadido newItem = convertToEntity(carritoAnadidoDTO);
            return convertToDto(carritoAnadidoRepository.save(newItem));
        }
    }

    @Transactional
    public Carrito_AnadidoDTO updateItemInCarrito(Integer id, Carrito_AnadidoDTO carritoAnadidoDTO) {
        return carritoAnadidoRepository.findById(id)
            .map(item -> {
                if (carritoAnadidoDTO.getCantidad() <= 0) {
                    throw new IllegalArgumentException("La cantidad a actualizar debe ser mayor que cero.");
                }
                item.setCantidad(carritoAnadidoDTO.getCantidad());
                Carrito_Anadido updatedItem = carritoAnadidoRepository.save(item);
                return convertToDto(updatedItem);
            }).orElseThrow(() -> new RuntimeException("Ítem de carrito no encontrado con ID: " + id));
    }

    @Transactional
    public void deleteItemFromCarrito(Integer id) {
        if (!carritoAnadidoRepository.existsById(id)) {
            throw new RuntimeException("Ítem de carrito con ID " + id + " no encontrado para eliminar.");
        }
        carritoAnadidoRepository.deleteById(id);
    }

    @Transactional
    public void clearCarrito(Integer carritoId) {
        List<Carrito_Anadido> items = carritoAnadidoRepository.findByCarritoIdCarrito(carritoId);
        carritoAnadidoRepository.deleteAll(items); 
    }
}