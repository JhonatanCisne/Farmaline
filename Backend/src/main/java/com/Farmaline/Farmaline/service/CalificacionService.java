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

    @Autowired
    private CalificacionRepository calificacionRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProductoRepository productoRepository;

    public List<CalificacionDTO> getAllCalificaciones() {
        return calificacionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<CalificacionDTO> getCalificacionById(Integer id) {
        return calificacionRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<CalificacionDTO> getCalificacionesByProductId(Integer idProducto) {
        return calificacionRepository.findByProducto_IdProducto(idProducto).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CalificacionDTO> getCalificacionesByUserId(Integer idUsuario) {
        return calificacionRepository.findByUsuario_IdUsuario(idUsuario).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<CalificacionDTO> getCalificacionByUserIdAndProductId(Integer idUsuario, Integer idProducto) {
        return calificacionRepository.findByUsuario_IdUsuarioAndProducto_IdProducto(idUsuario, idProducto)
                .map(this::convertToDTO);
    }

    public List<CalificacionDTO> getCalificacionesGreaterThanEqual(Integer idProducto, Integer puntuacion) {
        return calificacionRepository.findByProducto_IdProductoAndPuntuacionGreaterThanEqual(idProducto, puntuacion).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CalificacionDTO> getCalificacionesLessThanEqual(Integer idProducto, Integer puntuacion) {
        return calificacionRepository.findByProducto_IdProductoAndPuntuacionLessThanEqual(idProducto, puntuacion).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Double getAverageRatingForProduct(Integer idProducto) {
    List<CalificacionDTO> calificaciones = getCalificacionesByProductId(idProducto);
    if (calificaciones.isEmpty()) {
        return 0.0; 
    }

    double sum = calificaciones.stream()
                               .mapToInt(CalificacionDTO::getPuntuacion)
                               .sum();
    return sum / calificaciones.size();
}

    @Transactional
    public CalificacionDTO createCalificacion(CalificacionDTO calificacionDTO) {
        Usuario usuario = usuarioRepository.findById(calificacionDTO.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + calificacionDTO.getIdUsuario()));
        
        Producto producto = productoRepository.findById(calificacionDTO.getIdProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + calificacionDTO.getIdProducto()));

        if (calificacionRepository.existsByUsuario_IdUsuarioAndProducto_IdProducto(calificacionDTO.getIdUsuario(), calificacionDTO.getIdProducto())) {
            throw new IllegalStateException("El usuario ya ha calificado este producto.");
        }

        if (calificacionDTO.getPuntuacion() < 1 || calificacionDTO.getPuntuacion() > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5.");
        }

        Calificacion nuevaCalificacion = new Calificacion();
        nuevaCalificacion.setUsuario(usuario);
        nuevaCalificacion.setProducto(producto);
        nuevaCalificacion.setPuntuacion(calificacionDTO.getPuntuacion());

        Calificacion savedCalificacion = calificacionRepository.save(nuevaCalificacion);
        return convertToDTO(savedCalificacion);
    }

    @Transactional
    public Optional<CalificacionDTO> updateCalificacion(Integer id, CalificacionDTO calificacionDTO) {
        return calificacionRepository.findById(id).map(existingCalificacion -> {

            if (calificacionDTO.getPuntuacion() < 1 || calificacionDTO.getPuntuacion() > 5) {
                throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5.");
            }

            existingCalificacion.setPuntuacion(calificacionDTO.getPuntuacion());
            
            Calificacion updatedCalificacion = calificacionRepository.save(existingCalificacion);
            return convertToDTO(updatedCalificacion);
        });
    }

    @Transactional
    public boolean deleteCalificacion(Integer id) {
        if (calificacionRepository.existsById(id)) {
            calificacionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public void deleteCalificacionesByProductId(Integer idProducto) {
        calificacionRepository.deleteByProducto_IdProducto(idProducto);
    }

    @Transactional
    public void deleteCalificacionesByUserId(Integer idUsuario) {
        calificacionRepository.deleteByUsuario_IdUsuario(idUsuario);
    }

    private CalificacionDTO convertToDTO(Calificacion calificacion) {
        CalificacionDTO dto = new CalificacionDTO();
        dto.setIdCalificacion(calificacion.getIdCalificacion());
        dto.setPuntuacion(calificacion.getPuntuacion());
        dto.setIdUsuario(calificacion.getUsuario() != null ? calificacion.getUsuario().getIdUsuario() : null);
        dto.setIdProducto(calificacion.getProducto() != null ? calificacion.getProducto().getIdProducto() : null);
        return dto;
    }

}