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

    private final DobleVerificacionRepository dobleVerificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final RepartidorRepository repartidorRepository;
    private final PedidoRepository pedidoRepository;

    @Autowired
    public DobleVerificacionService(DobleVerificacionRepository dobleVerificacionRepository, UsuarioRepository usuarioRepository, RepartidorRepository repartidorRepository, PedidoRepository pedidoRepository) {
        this.dobleVerificacionRepository = dobleVerificacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.repartidorRepository = repartidorRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public List<DobleVerificacionDTO> obtenerTodasDobleVerificaciones() {
        return dobleVerificacionRepository.findAll().stream()
                .map(this::convertirADobleVerificacionDTO)
                .collect(Collectors.toList());
    }

    public Optional<DobleVerificacionDTO> obtenerDobleVerificacionPorId(Integer id) {
        return dobleVerificacionRepository.findById(id)
                .map(this::convertirADobleVerificacionDTO);
    }

    @Transactional
    public DobleVerificacionDTO crearDobleVerificacion(DobleVerificacionDTO dobleVerificacionDTO) {
        Doble_Verificacion dobleVerificacion = new Doble_Verificacion();
        dobleVerificacion.setEstadoUsuario(dobleVerificacionDTO.getEstadoUsuario());
        dobleVerificacion.setEstadoRepartidor(dobleVerificacionDTO.getEstadoRepartidor());

        Usuario usuario = usuarioRepository.findById(dobleVerificacionDTO.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        dobleVerificacion.setUsuario(usuario);

        Repartidor repartidor = repartidorRepository.findById(dobleVerificacionDTO.getIdRepartidor())
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));
        dobleVerificacion.setRepartidor(repartidor);

        Pedido pedido = pedidoRepository.findById(dobleVerificacionDTO.getIdPedido())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        dobleVerificacion.setPedido(pedido);

        dobleVerificacion = dobleVerificacionRepository.save(dobleVerificacion);
        return convertirADobleVerificacionDTO(dobleVerificacion);
    }

    @Transactional
    public Optional<DobleVerificacionDTO> actualizarDobleVerificacion(Integer id, DobleVerificacionDTO dobleVerificacionDTO) {
        return dobleVerificacionRepository.findById(id)
                .map(dvExistente -> {
                    dvExistente.setEstadoUsuario(dobleVerificacionDTO.getEstadoUsuario());
                    dvExistente.setEstadoRepartidor(dobleVerificacionDTO.getEstadoRepartidor());

                    if (dobleVerificacionDTO.getIdUsuario() != null) {
                        Usuario usuario = usuarioRepository.findById(dobleVerificacionDTO.getIdUsuario())
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                        dvExistente.setUsuario(usuario);
                    }

                    if (dobleVerificacionDTO.getIdRepartidor() != null) {
                        Repartidor repartidor = repartidorRepository.findById(dobleVerificacionDTO.getIdRepartidor())
                                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));
                        dvExistente.setRepartidor(repartidor);
                    }

                    if (dobleVerificacionDTO.getIdPedido() != null) {
                        Pedido pedido = pedidoRepository.findById(dobleVerificacionDTO.getIdPedido())
                                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
                        dvExistente.setPedido(pedido);
                    }
                    return convertirADobleVerificacionDTO(dobleVerificacionRepository.save(dvExistente));
                });
    }

    public boolean eliminarDobleVerificacion(Integer id) {
        if (dobleVerificacionRepository.existsById(id)) {
            dobleVerificacionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private DobleVerificacionDTO convertirADobleVerificacionDTO(Doble_Verificacion dobleVerificacion) {
        DobleVerificacionDTO dto = new DobleVerificacionDTO();
        dto.setIdDobleVerificacion(dobleVerificacion.getIdDobleVerifiacion());
        dto.setEstadoUsuario(dobleVerificacion.getEstadoUsuario());
        dto.setEstadoRepartidor(dobleVerificacion.getEstadoRepartidor());
        if (dobleVerificacion.getUsuario() != null) {
            dto.setIdUsuario(dobleVerificacion.getUsuario().getIdUsuario());
            dto.setNombreUsuario(dobleVerificacion.getUsuario().getNombre() + " " + dobleVerificacion.getUsuario().getApellido());
        }
        if (dobleVerificacion.getRepartidor() != null) {
            dto.setIdRepartidor(dobleVerificacion.getRepartidor().getIdRepartidor());
            dto.setNombreRepartidor(dobleVerificacion.getRepartidor().getNombre() + " " + dobleVerificacion.getRepartidor().getApellido());
        }
        if (dobleVerificacion.getPedido() != null) {
            dto.setIdPedido(dobleVerificacion.getPedido().getIdPedido());
        }
        return dto;
    }
}