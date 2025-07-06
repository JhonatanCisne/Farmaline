package com.farmaline.farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.dto.DobleVerificacionDTO;
import com.farmaline.farmaline.model.Doble_Verificacion;
import com.farmaline.farmaline.model.Pedido; 
import com.farmaline.farmaline.model.Repartidor;
import com.farmaline.farmaline.model.Usuario;
import com.farmaline.farmaline.repository.DobleVerificacionRepository;
import com.farmaline.farmaline.repository.PedidoRepository;
import com.farmaline.farmaline.repository.RepartidorRepository;
import com.farmaline.farmaline.repository.UsuarioRepository;

@Service
public class DobleVerificacionService {

    @Autowired
    private DobleVerificacionRepository dobleVerificacionRepository;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RepartidorRepository repartidorRepository;

    public List<DobleVerificacionDTO> getAllDobleVerificaciones() {
        return dobleVerificacionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<DobleVerificacionDTO> getDobleVerificacionById(Integer id) {
        return dobleVerificacionRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<DobleVerificacionDTO> getDobleVerificacionByPedidoId(Integer idPedido) {
        return dobleVerificacionRepository.findByPedido_IdPedido(idPedido)
                .map(this::convertToDTO);
    }

    public List<DobleVerificacionDTO> getDobleVerificacionesByUsuarioId(Integer idUsuario) {
        return dobleVerificacionRepository.findByUsuario_IdUsuario(idUsuario).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DobleVerificacionDTO> getDobleVerificacionesByRepartidorId(Integer idRepartidor) {
        return dobleVerificacionRepository.findByRepartidor_IdRepartidor(idRepartidor).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DobleVerificacionDTO createDobleVerificacion(DobleVerificacionDTO dobleVerificacionDTO) {
        Pedido pedido = pedidoRepository.findById(dobleVerificacionDTO.getIdPedido())
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + dobleVerificacionDTO.getIdPedido()));
        
        if (dobleVerificacionRepository.existsByPedido_IdPedido(dobleVerificacionDTO.getIdPedido())) {
            throw new IllegalStateException("Ya existe un registro de doble verificación para el Pedido con ID: " + dobleVerificacionDTO.getIdPedido());
        }

        Usuario usuario = usuarioRepository.findById(dobleVerificacionDTO.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + dobleVerificacionDTO.getIdUsuario()));

        Repartidor repartidor = repartidorRepository.findById(dobleVerificacionDTO.getIdRepartidor())
                .orElseThrow(() -> new IllegalArgumentException("Repartidor no encontrado con ID: " + dobleVerificacionDTO.getIdRepartidor()));

        Doble_Verificacion nuevaVerificacion = new Doble_Verificacion();
        nuevaVerificacion.setPedido(pedido);
        nuevaVerificacion.setUsuario(usuario);
        nuevaVerificacion.setRepartidor(repartidor);
        nuevaVerificacion.setEstadoUsuario(dobleVerificacionDTO.getEstadoUsuario() != null ? dobleVerificacionDTO.getEstadoUsuario() : "Pendiente");
        nuevaVerificacion.setEstadoRepartidor(dobleVerificacionDTO.getEstadoRepartidor() != null ? dobleVerificacionDTO.getEstadoRepartidor() : "Pendiente");

        Doble_Verificacion savedVerificacion = dobleVerificacionRepository.save(nuevaVerificacion);
        return convertToDTO(savedVerificacion);
    }

    @Transactional
    public Optional<DobleVerificacionDTO> updateEstadoUsuario(Integer idDobleVerificacion, String nuevoEstado) {
        return dobleVerificacionRepository.findById(idDobleVerificacion).map(verificacion -> {
            verificacion.setEstadoUsuario(nuevoEstado);
            Doble_Verificacion updatedVerificacion = dobleVerificacionRepository.save(verificacion);
            return convertToDTO(updatedVerificacion);
        });
    }

    @Transactional
    public Optional<DobleVerificacionDTO> updateEstadoRepartidor(Integer idDobleVerificacion, String nuevoEstado) {
        return dobleVerificacionRepository.findById(idDobleVerificacion).map(verificacion -> {
            verificacion.setEstadoRepartidor(nuevoEstado);
            Doble_Verificacion updatedVerificacion = dobleVerificacionRepository.save(verificacion);
            return convertToDTO(updatedVerificacion);
        });
    }

    @Transactional
    public boolean deleteDobleVerificacion(Integer id) {
        if (dobleVerificacionRepository.existsById(id)) {
            dobleVerificacionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private DobleVerificacionDTO convertToDTO(Doble_Verificacion verificacion) {
        DobleVerificacionDTO dto = new DobleVerificacionDTO();
        dto.setIdDobleVerifiacion(verificacion.getIdDobleVerifiacion());
        dto.setEstadoUsuario(verificacion.getEstadoUsuario());
        dto.setEstadoRepartidor(verificacion.getEstadoRepartidor());
        dto.setIdUsuario(verificacion.getUsuario() != null ? verificacion.getUsuario().getIdUsuario() : null);
        dto.setIdRepartidor(verificacion.getRepartidor() != null ? verificacion.getRepartidor().getIdRepartidor() : null);
        dto.setIdPedido(verificacion.getPedido() != null ? verificacion.getPedido().getIdPedido() : null);
        return dto;
    }
}