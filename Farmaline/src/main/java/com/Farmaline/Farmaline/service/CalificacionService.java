package com.Farmaline.Farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Farmaline.Farmaline.dto.CalificacionDTO;
import com.Farmaline.Farmaline.model.Calificacion;
import com.Farmaline.Farmaline.model.Producto;
import com.Farmaline.Farmaline.model.Usuario;
import com.Farmaline.Farmaline.repository.CalificacionRepository;
import com.Farmaline.Farmaline.repository.ProductoRepository;
import com.Farmaline.Farmaline.repository.UsuarioRepository;

@Service
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository; 

    @Autowired
    public CalificacionService(
            CalificacionRepository calificacionRepository,
            UsuarioRepository usuarioRepository,
            ProductoRepository productoRepository) {
        this.calificacionRepository = calificacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    private CalificacionDTO convertToDto(Calificacion calificacion) {
        if (calificacion == null) {
            return null;
        }
        CalificacionDTO dto = new CalificacionDTO();
        dto.setIdCalificacion(calificacion.getIdCalificacion());
        dto.setPuntuacion(calificacion.getPuntuacion());
        if (calificacion.getUsuario() != null) {
            dto.setIdUsuario(calificacion.getUsuario().getIdUsuario());
        }
        if (calificacion.getProducto() != null) {
            dto.setIdProducto(calificacion.getProducto().getIdProducto());
        }
        return dto;
    }

    private Calificacion convertToEntity(CalificacionDTO dto) {
        if (dto == null) {
            return null;
        }
        Calificacion calificacion = new Calificacion();
        calificacion.setPuntuacion(dto.getPuntuacion());

        if (dto.getIdUsuario() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getIdUsuario()));
            calificacion.setUsuario(usuario);
        } else {
            throw new IllegalArgumentException("ID de usuario es obligatorio para la calificación.");
        }

        if (dto.getIdProducto() != null) {
            Producto producto = productoRepository.findById(dto.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.getIdProducto()));
            calificacion.setProducto(producto);
        } else {
            throw new IllegalArgumentException("ID de producto es obligatorio para la calificación.");
        }
        return calificacion;
    }

    @Transactional(readOnly = true)
    public List<CalificacionDTO> findAllCalificaciones() {
        return calificacionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<CalificacionDTO> findCalificacionById(Integer id) {
        return calificacionRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<CalificacionDTO> findCalificacionesByProductoId(Integer productoId) {
        return calificacionRepository.findByProductoIdProducto(productoId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CalificacionDTO> findCalificacionesByUsuarioId(Integer usuarioId) {
        return calificacionRepository.findByUsuarioIdUsuario(usuarioId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<CalificacionDTO> findCalificacionByUsuarioAndProducto(Integer usuarioId, Integer productoId) {
        return calificacionRepository.findByUsuarioIdUsuarioAndProductoIdProducto(usuarioId, productoId)
                .map(this::convertToDto);
    }

    @Transactional
    public CalificacionDTO saveCalificacion(CalificacionDTO calificacionDTO) {
        if (calificacionDTO.getPuntuacion() < 1 || calificacionDTO.getPuntuacion() > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5.");
        }

        if (calificacionRepository.findByUsuarioIdUsuarioAndProductoIdProducto(
                calificacionDTO.getIdUsuario(), calificacionDTO.getIdProducto()).isPresent()) {
            throw new IllegalStateException("El usuario ya ha calificado este producto.");
        }

        Calificacion calificacion = convertToEntity(calificacionDTO);
        Calificacion savedCalificacion = calificacionRepository.save(calificacion);
        return convertToDto(savedCalificacion);
    }

    @Transactional
    public CalificacionDTO updateCalificacion(Integer id, CalificacionDTO calificacionDTO) {
        return calificacionRepository.findById(id)
            .map(calificacion -> {
                if (calificacionDTO.getPuntuacion() != null && (calificacionDTO.getPuntuacion() < 1 || calificacionDTO.getPuntuacion() > 5)) {
                    throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5.");
                }
                calificacion.setPuntuacion(calificacionDTO.getPuntuacion());
                Calificacion updatedCalificacion = calificacionRepository.save(calificacion);
                return convertToDto(updatedCalificacion);
            }).orElseThrow(() -> new RuntimeException("Calificación no encontrada con ID: " + id));
    }

    @Transactional
    public void deleteCalificacionById(Integer id) {
        if (!calificacionRepository.existsById(id)) {
            throw new RuntimeException("Calificación con ID " + id + " no encontrada para eliminar.");
        }
        calificacionRepository.deleteById(id);
    }
}