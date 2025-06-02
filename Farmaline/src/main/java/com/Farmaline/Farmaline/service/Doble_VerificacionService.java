package com.Farmaline.Farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Farmaline.Farmaline.dto.Doble_VerificacionDTO;
import com.Farmaline.Farmaline.model.Doble_Verificacion;
import com.Farmaline.Farmaline.model.Pedido;
import com.Farmaline.Farmaline.model.Repartidor;
import com.Farmaline.Farmaline.model.Usuario;
import com.Farmaline.Farmaline.repository.Doble_VerificacionRepository;
import com.Farmaline.Farmaline.repository.PedidoRepository;
import com.Farmaline.Farmaline.repository.RepartidorRepository;
import com.Farmaline.Farmaline.repository.UsuarioRepository;

@Service
public class Doble_VerificacionService {

    private final Doble_VerificacionRepository dobleVerificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final RepartidorRepository repartidorRepository;
    private final PedidoRepository pedidoRepository;

    @Autowired
    public Doble_VerificacionService(
            Doble_VerificacionRepository dobleVerificacionRepository,
            UsuarioRepository usuarioRepository,
            RepartidorRepository repartidorRepository,
            PedidoRepository pedidoRepository) {
        this.dobleVerificacionRepository = dobleVerificacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.repartidorRepository = repartidorRepository;
        this.pedidoRepository = pedidoRepository;
    }

    private Doble_VerificacionDTO convertToDto(Doble_Verificacion dobleVerificacion) {
        if (dobleVerificacion == null) {
            return null;
        }
        Doble_VerificacionDTO dto = new Doble_VerificacionDTO();
        dto.setIdDobleVerificacion(dobleVerificacion.getIdDobleVerifiacion());
        dto.setEstadoUsuario(dobleVerificacion.getEstadoUsuario());
        dto.setEstadoRepartidor(dobleVerificacion.getEstadoRepartidor());
        if (dobleVerificacion.getUsuario() != null) {
            dto.setIdUsuario(dobleVerificacion.getUsuario().getIdUsuario());
        }
        if (dobleVerificacion.getRepartidor() != null) {
            dto.setIdRepartidor(dobleVerificacion.getRepartidor().getIdRepartidor());
        }
        if (dobleVerificacion.getPedido() != null) {
            dto.setIdPedido(dobleVerificacion.getPedido().getIdPedido());
        }
        return dto;
    }

    private Doble_Verificacion convertToEntity(Doble_VerificacionDTO dto) {
        if (dto == null) {
            return null;
        }
        Doble_Verificacion dobleVerificacion = new Doble_Verificacion();

        dobleVerificacion.setEstadoUsuario(dto.getEstadoUsuario());
        dobleVerificacion.setEstadoRepartidor(dto.getEstadoRepartidor());

        if (dto.getIdUsuario() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getIdUsuario()));
            dobleVerificacion.setUsuario(usuario);
        } else {
            throw new IllegalArgumentException("ID de usuario es obligatorio para Doble_Verificacion.");
        }

        if (dto.getIdRepartidor() != null) {
            Repartidor repartidor = repartidorRepository.findById(dto.getIdRepartidor())
                    .orElseThrow(() -> new RuntimeException("Repartidor no encontrado con ID: " + dto.getIdRepartidor()));
            dobleVerificacion.setRepartidor(repartidor);
        } else {
            throw new IllegalArgumentException("ID de repartidor es obligatorio para Doble_Verificacion.");
        }

        if (dto.getIdPedido() != null) {
            Pedido pedido = pedidoRepository.findById(dto.getIdPedido())
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + dto.getIdPedido()));
            dobleVerificacion.setPedido(pedido);
        } else {
            throw new IllegalArgumentException("ID de pedido es obligatorio para Doble_Verificacion.");
        }
        return dobleVerificacion;
    }

    @Transactional(readOnly = true)
    public List<Doble_VerificacionDTO> findAllDobleVerificaciones() {
        return dobleVerificacionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Doble_VerificacionDTO> findDobleVerificacionById(Integer id) {
        return dobleVerificacionRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Optional<Doble_VerificacionDTO> findDobleVerificacionByPedidoId(Integer pedidoId) {
        return dobleVerificacionRepository.findByPedidoIdPedido(pedidoId)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<Doble_VerificacionDTO> findDobleVerificacionesByUsuarioId(Integer usuarioId) {
        return dobleVerificacionRepository.findByUsuarioIdUsuario(usuarioId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Doble_VerificacionDTO> findDobleVerificacionesByRepartidorId(Integer repartidorId) {
        return dobleVerificacionRepository.findByRepartidorIdRepartidor(repartidorId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Doble_VerificacionDTO createDobleVerificacion(Doble_VerificacionDTO dobleVerificacionDTO) {
        if (dobleVerificacionRepository.findByPedidoIdPedido(dobleVerificacionDTO.getIdPedido()).isPresent()) {
            throw new IllegalStateException("El pedido con ID " + dobleVerificacionDTO.getIdPedido() + " ya tiene una doble verificación asociada.");
        }

        Doble_Verificacion dobleVerificacion = convertToEntity(dobleVerificacionDTO);
        Doble_Verificacion savedDobleVerificacion = dobleVerificacionRepository.save(dobleVerificacion);
        return convertToDto(savedDobleVerificacion);
    }

    @Transactional
    public Doble_VerificacionDTO updateDobleVerificacion(Integer id, Doble_VerificacionDTO dobleVerificacionDTO) {
        return dobleVerificacionRepository.findById(id)
            .map(dv -> {
                if (dobleVerificacionDTO.getEstadoUsuario() != null) {
                    dv.setEstadoUsuario(dobleVerificacionDTO.getEstadoUsuario());
                }
                if (dobleVerificacionDTO.getEstadoRepartidor() != null) {
                    dv.setEstadoRepartidor(dobleVerificacionDTO.getEstadoRepartidor());
                }
                Doble_Verificacion updatedDv = dobleVerificacionRepository.save(dv);
                return convertToDto(updatedDv);
            }).orElseThrow(() -> new RuntimeException("Doble Verificación no encontrada con ID: " + id));
    }

    @Transactional
    public void deleteDobleVerificacion(Integer id) {
        if (!dobleVerificacionRepository.existsById(id)) {
            throw new RuntimeException("Doble Verificación con ID " + id + " no encontrada para eliminar.");
        }
        dobleVerificacionRepository.deleteById(id);
    }

    @Transactional
    public Doble_VerificacionDTO updateEstadoUsuario(Integer id, String nuevoEstado) {
        return dobleVerificacionRepository.findById(id)
            .map(dv -> {
                dv.setEstadoUsuario(nuevoEstado);
                Doble_Verificacion updatedDv = dobleVerificacionRepository.save(dv);
                return convertToDto(updatedDv);
            }).orElseThrow(() -> new RuntimeException("Doble Verificación no encontrada con ID: " + id));
    }

    @Transactional
    public Doble_VerificacionDTO updateEstadoRepartidor(Integer id, String nuevoEstado) {
        return dobleVerificacionRepository.findById(id)
            .map(dv -> {
                dv.setEstadoRepartidor(nuevoEstado);
                Doble_Verificacion updatedDv = dobleVerificacionRepository.save(dv);
                return convertToDto(updatedDv);
            }).orElseThrow(() -> new RuntimeException("Doble Verificación no encontrada con ID: " + id));
    }
}