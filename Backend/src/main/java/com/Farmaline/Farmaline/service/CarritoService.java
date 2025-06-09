package com.Farmaline.Farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Farmaline.Farmaline.dto.CarritoDTO;
import com.Farmaline.Farmaline.model.Carrito;
import com.Farmaline.Farmaline.model.Usuario;
import com.Farmaline.Farmaline.repository.CarritoRepository;
import com.Farmaline.Farmaline.repository.UsuarioRepository;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public CarritoService(CarritoRepository carritoRepository, UsuarioRepository usuarioRepository) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private CarritoDTO convertToDto(Carrito carrito) {
        if (carrito == null) {
            return null;
        }
        CarritoDTO dto = new CarritoDTO();
        dto.setIdCarrito(carrito.getIdCarrito());
        if (carrito.getUsuario() != null) {
            dto.setIdUsuario(carrito.getUsuario().getIdUsuario());
        }
        return dto;
    }

    private Carrito convertToEntity(CarritoDTO dto) {
        if (dto == null) {
            return null;
        }
        Carrito carrito = new Carrito();

        if (dto.getIdUsuario() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getIdUsuario()));
            carrito.setUsuario(usuario);
        } else {
            throw new IllegalArgumentException("ID de usuario es obligatorio para crear un carrito.");
        }
        return carrito;
    }

    @Transactional(readOnly = true)
    public List<CarritoDTO> findAllCarritos() {
        return carritoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<CarritoDTO> findCarritoById(Integer id) {
        return carritoRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Optional<CarritoDTO> findCarritoByUsuarioId(Integer usuarioId) {
        return carritoRepository.findByUsuarioIdUsuario(usuarioId)
                .map(this::convertToDto);
    }

    @Transactional
    public CarritoDTO createCarrito(CarritoDTO carritoDTO) {
        if (carritoRepository.existsByUsuarioIdUsuario(carritoDTO.getIdUsuario())) {
            throw new IllegalStateException("El usuario con ID " + carritoDTO.getIdUsuario() + " ya tiene un carrito.");
        }

        Carrito carrito = convertToEntity(carritoDTO);
        Carrito savedCarrito = carritoRepository.save(carrito);
        return convertToDto(savedCarrito);
    }

    @Transactional
    public void deleteCarritoById(Integer id) {
        if (!carritoRepository.existsById(id)) {
            throw new RuntimeException("Carrito con ID " + id + " no encontrado para eliminar.");
        }
        carritoRepository.deleteById(id);
    }
}