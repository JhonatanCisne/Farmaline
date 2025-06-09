package com.Farmaline.Farmaline.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Farmaline.Farmaline.dto.PedidoDTO;
import com.Farmaline.Farmaline.model.Carrito;
import com.Farmaline.Farmaline.model.Pedido;
import com.Farmaline.Farmaline.model.Repartidor;
import com.Farmaline.Farmaline.model.Usuario;
import com.Farmaline.Farmaline.repository.CarritoRepository;
import com.Farmaline.Farmaline.repository.PedidoRepository;
import com.Farmaline.Farmaline.repository.RepartidorRepository;
import com.Farmaline.Farmaline.repository.UsuarioRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarritoRepository carritoRepository;
    private final RepartidorRepository repartidorRepository;

    @Autowired
    public PedidoService(
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository,
            CarritoRepository carritoRepository,
            RepartidorRepository repartidorRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.carritoRepository = carritoRepository;
        this.repartidorRepository = repartidorRepository;
    }

    private PedidoDTO convertToDto(Pedido pedido) {
        if (pedido == null) {
            return null;
        }
        PedidoDTO dto = new PedidoDTO();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFecha(pedido.getFecha());
        dto.setHora(pedido.getHora());
        if (pedido.getUsuario() != null) {
            dto.setIdUsuario(pedido.getUsuario().getIdUsuario());
        }
        if (pedido.getCarrito() != null) {
            dto.setIdCarrito(pedido.getCarrito().getIdCarrito());
        }
        if (pedido.getRepartidor() != null) {
            dto.setIdRepartidor(pedido.getRepartidor().getIdRepartidor());
        }
        return dto;
    }

    private Pedido convertToEntity(PedidoDTO dto) {
        if (dto == null) {
            return null;
        }
        Pedido pedido = new Pedido();
        pedido.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDate.now());
        pedido.setHora(dto.getHora() != null ? dto.getHora() : LocalTime.now());

        if (dto.getIdUsuario() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getIdUsuario()));
            pedido.setUsuario(usuario);
        } else {
            throw new IllegalArgumentException("ID de usuario es obligatorio para el pedido.");
        }

        if (dto.getIdCarrito() != null) {
            Carrito carrito = carritoRepository.findById(dto.getIdCarrito())
                    .orElseThrow(() -> new RuntimeException("Carrito no encontrado con ID: " + dto.getIdCarrito()));
            pedido.setCarrito(carrito);
        } else {
            throw new IllegalArgumentException("ID de carrito es obligatorio para el pedido.");
        }

        if (dto.getIdRepartidor() != null) {
            Repartidor repartidor = repartidorRepository.findById(dto.getIdRepartidor())
                    .orElseThrow(() -> new RuntimeException("Repartidor no encontrado con ID: " + dto.getIdRepartidor()));
            pedido.setRepartidor(repartidor);
        } else {
            pedido.setRepartidor(null);
        }
        return pedido;
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> findAllPedidos() {
        return pedidoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<PedidoDTO> findPedidoById(Integer id) {
        return pedidoRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> findPedidosByUsuarioId(Integer usuarioId) {
        return pedidoRepository.findByUsuarioIdUsuario(usuarioId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> findPedidosByRepartidorId(Integer repartidorId) {
        return pedidoRepository.findByRepartidorIdRepartidor(repartidorId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> findPedidosByFecha(LocalDate fecha) {
        return pedidoRepository.findByFecha(fecha).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PedidoDTO createPedido(PedidoDTO pedidoDTO) {
        if (pedidoDTO.getIdCarrito() != null && pedidoRepository.findByCarritoIdCarrito(pedidoDTO.getIdCarrito()).isPresent()) {
            throw new IllegalStateException("El carrito con ID " + pedidoDTO.getIdCarrito() + " ya está asociado a un pedido.");
        }

        Pedido pedido = convertToEntity(pedidoDTO);
        Pedido savedPedido = pedidoRepository.save(pedido);
        return convertToDto(savedPedido);
    }

    @Transactional
    public PedidoDTO assignRepartidorToPedido(Integer pedidoId, Integer repartidorId) {
        return pedidoRepository.findById(pedidoId)
            .map(pedido -> {
                Repartidor repartidor = repartidorRepository.findById(repartidorId)
                    .orElseThrow(() -> new RuntimeException("Repartidor no encontrado con ID: " + repartidorId));
                pedido.setRepartidor(repartidor);
                Pedido updatedPedido = pedidoRepository.save(pedido);
                return convertToDto(updatedPedido);
            }).orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));
    }

    @Transactional
    public void deletePedido(Integer id) {
        if (!pedidoRepository.existsById(id)) {
            throw new RuntimeException("Pedido con ID " + id + " no encontrado para eliminar.");
        }
        pedidoRepository.deleteById(id);
    }
}