package com.farmaline.farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.dto.CalificacionDTO;
import com.farmaline.farmaline.model.Calificacion;
import com.farmaline.farmaline.model.Producto;
import com.farmaline.farmaline.model.Usuario;
import com.farmaline.farmaline.repository.CalificacionRepository;
import com.farmaline.farmaline.repository.ProductoRepository;
import com.farmaline.farmaline.repository.UsuarioRepository;

@Service
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public CalificacionService(CalificacionRepository calificacionRepository, UsuarioRepository usuarioRepository, ProductoRepository productoRepository) {
        this.calificacionRepository = calificacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    public List<CalificacionDTO> obtenerTodasCalificaciones() {
        return calificacionRepository.findAll().stream()
                .map(this::convertirACalificacionDTO)
                .collect(Collectors.toList());
    }

    public Optional<CalificacionDTO> obtenerCalificacionPorId(Integer id) {
        return calificacionRepository.findById(id)
                .map(this::convertirACalificacionDTO);
    }

    @Transactional
    public CalificacionDTO crearCalificacion(CalificacionDTO calificacionDTO) {
        Calificacion calificacion = new Calificacion();
        calificacion.setPuntuacion(calificacionDTO.getPuntuacion());

        Usuario usuario = usuarioRepository.findById(calificacionDTO.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        calificacion.setUsuario(usuario);

        Producto producto = productoRepository.findById(calificacionDTO.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        calificacion.setProducto(producto);

        calificacion = calificacionRepository.save(calificacion);
        return convertirACalificacionDTO(calificacion);
    }

    @Transactional
    public Optional<CalificacionDTO> actualizarCalificacion(Integer id, CalificacionDTO calificacionDTO) {
        return calificacionRepository.findById(id)
                .map(calificacionExistente -> {
                    calificacionExistente.setPuntuacion(calificacionDTO.getPuntuacion());

                    if (calificacionDTO.getIdUsuario() != null) {
                        Usuario usuario = usuarioRepository.findById(calificacionDTO.getIdUsuario())
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                        calificacionExistente.setUsuario(usuario);
                    }

                    if (calificacionDTO.getIdProducto() != null) {
                        Producto producto = productoRepository.findById(calificacionDTO.getIdProducto())
                                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                        calificacionExistente.setProducto(producto);
                    }
                    return convertirACalificacionDTO(calificacionRepository.save(calificacionExistente));
                });
    }

    public boolean eliminarCalificacion(Integer id) {
        if (calificacionRepository.existsById(id)) {
            calificacionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private CalificacionDTO convertirACalificacionDTO(Calificacion calificacion) {
        CalificacionDTO dto = new CalificacionDTO();
        dto.setIdCalificacion(calificacion.getIdCalificacion());
        dto.setPuntuacion(calificacion.getPuntuacion());
        if (calificacion.getUsuario() != null) {
            dto.setIdUsuario(calificacion.getUsuario().getIdUsuario());
            dto.setNombreUsuario(calificacion.getUsuario().getNombre() + " " + calificacion.getUsuario().getApellido());
        }
        if (calificacion.getProducto() != null) {
            dto.setIdProducto(calificacion.getProducto().getIdProducto());
            dto.setNombreProducto(calificacion.getProducto().getNombre());
        }
        return dto;
    }
}