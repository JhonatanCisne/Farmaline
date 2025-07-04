package com.Farmaline.farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.dto.CarritoDTO;
import com.farmaline.farmaline.model.Carrito;
import com.farmaline.farmaline.model.Usuario;
import com.farmaline.farmaline.repository.CarritoRepository;
import com.farmaline.farmaline.repository.UsuarioRepository;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarritoAnadidoService carritoAnadidoService;

    @Autowired
    public CarritoService(CarritoRepository carritoRepository, UsuarioRepository usuarioRepository, CarritoAnadidoService carritoAnadidoService) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.carritoAnadidoService = carritoAnadidoService;
    }

    public List<CarritoDTO> obtenerTodosCarritos() {
        return carritoRepository.findAll().stream()
                .map(this::convertirACarritoDTO)
                .collect(Collectors.toList());
    }

    public Optional<CarritoDTO> obtenerCarritoPorId(Integer id) {
        return carritoRepository.findById(id)
                .map(this::convertirACarritoDTO);
    }

    public Optional<CarritoDTO> obtenerCarritoPorIdUsuario(Integer idUsuario) {
        return carritoRepository.findByUsuarioIdUsuario(idUsuario)
                .map(this::convertirACarritoDTO);
    }

    @Transactional
    public CarritoDTO crearCarrito(CarritoDTO carritoDTO) {
        Carrito carrito = new Carrito();
        Usuario usuario = usuarioRepository.findById(carritoDTO.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        carrito.setUsuario(usuario);
        carrito = carritoRepository.save(carrito);
        return convertirACarritoDTO(carrito);
    }

    @Transactional
    public Optional<CarritoDTO> actualizarCarrito(Integer id, CarritoDTO carritoDTO) {
        return carritoRepository.findById(id)
                .map(carritoExistente -> {
                    if (carritoDTO.getIdUsuario() != null) {
                        Usuario usuario = usuarioRepository.findById(carritoDTO.getIdUsuario())
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                        carritoExistente.setUsuario(usuario);
                    }
                    return convertirACarritoDTO(carritoRepository.save(carritoExistente));
                });
    }

    @Transactional
    public boolean eliminarCarrito(Integer id) {
        if (carritoRepository.existsById(id)) {
            carritoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private CarritoDTO convertirACarritoDTO(Carrito carrito) {
        CarritoDTO dto = new CarritoDTO();
        dto.setIdCarrito(carrito.getIdCarrito());
        if (carrito.getUsuario() != null) {
            dto.setIdUsuario(carrito.getUsuario().getIdUsuario());
            dto.setNombreUsuario(carrito.getUsuario().getNombre() + " " + carrito.getUsuario().getApellido());
        }
        dto.setItemsCarrito(carritoAnadidoService.obtenerItemsCarritoPorIdCarrito(carrito.getIdCarrito()));
        return dto;
    }
}