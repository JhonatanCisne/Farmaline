package com.farmaline.farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // Necesario para eliminar ítems del carrito
import org.springframework.transaction.annotation.Transactional; // Asumiendo que tienes un CarritoDTO

import com.farmaline.farmaline.dto.CarritoDTO;
import com.farmaline.farmaline.model.Carrito;
import com.farmaline.farmaline.model.Usuario;
import com.farmaline.farmaline.repository.CarritoAnadidoRepository;
import com.farmaline.farmaline.repository.CarritoRepository;
import com.farmaline.farmaline.repository.UsuarioRepository;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CarritoAnadidoRepository carritoAnadidoRepository; // Para limpiar los ítems del carrito si se elimina el carrito

    // Asumimos un CarritoDTO simple para este servicio, ya que el Carrito_AnadidoService maneja los detalles
    // Si tu CarritoDTO incluye los Carrito_Anadido, la lógica de conversión aquí debería ser más compleja.

    public List<CarritoDTO> getAllCarritos() {
        return carritoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<CarritoDTO> getCarritoById(Integer id) {
        return carritoRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<CarritoDTO> getCarritoByUserId(Integer idUsuario) {
        return carritoRepository.findByUsuario_IdUsuario(idUsuario)
                .map(this::convertToDTO);
    }

    @Transactional
    public CarritoDTO createCarrito(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));

        // Verificar si el usuario ya tiene un carrito
        if (carritoRepository.findByUsuario_IdUsuario(idUsuario).isPresent()) {
            throw new IllegalStateException("El usuario con ID " + idUsuario + " ya tiene un carrito.");
        }

        Carrito nuevoCarrito = new Carrito();
        nuevoCarrito.setUsuario(usuario);
        
        Carrito savedCarrito = carritoRepository.save(nuevoCarrito);
        return convertToDTO(savedCarrito);
    }

    @Transactional
    public boolean deleteCarrito(Integer idCarrito) {
        // Primero, elimina todos los Carrito_Anadido asociados a este carrito
        carritoAnadidoRepository.deleteByCarrito_IdCarrito(idCarrito);

        // Luego, elimina el carrito
        if (carritoRepository.existsById(idCarrito)) {
            carritoRepository.deleteById(idCarrito);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteCarritoByUserId(Integer idUsuario) {
        Optional<Carrito> carritoOptional = carritoRepository.findByUsuario_IdUsuario(idUsuario);
        if (carritoOptional.isPresent()) {
            Integer idCarrito = carritoOptional.get().getIdCarrito();
            // Primero, elimina todos los Carrito_Anadido asociados a este carrito
            carritoAnadidoRepository.deleteByCarrito_IdCarrito(idCarrito);
            // Luego, elimina el carrito
            carritoRepository.deleteByUsuario_IdUsuario(idUsuario);
            return true;
        }
        return false;
    }

    // Suponiendo un CarritoDTO simple con solo ID_Carrito e ID_Usuario
    private CarritoDTO convertToDTO(Carrito carrito) {
        CarritoDTO dto = new CarritoDTO();
        dto.setIdCarrito(carrito.getIdCarrito());
        dto.setIdUsuario(carrito.getUsuario() != null ? carrito.getUsuario().getIdUsuario() : null);
        return dto;
    }

}