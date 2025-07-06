package com.farmaline.farmaline.service;

import com.farmaline.farmaline.model.Repartidor;
import com.farmaline.farmaline.repository.RepartidorRepository;
import com.farmaline.farmaline.repository.PedidoRepository;
import com.farmaline.farmaline.repository.DobleVerificacionRepository;

import com.farmaline.farmaline.dto.RepartidorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RepartidorService {

    @Autowired
    private RepartidorRepository repartidorRepository;

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private DobleVerificacionRepository dobleVerificacionRepository;

    // --- Métodos de Gestión de Repartidores ---

    // Obtener todos los repartidores
    public List<RepartidorDTO> getAllRepartidores() {
        return repartidorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Obtener un repartidor por ID
    public Optional<RepartidorDTO> getRepartidorById(Integer id) {
        return repartidorRepository.findById(id)
                .map(this::convertToDTO);
    }

    // Lógica de negocio: Registrar un nuevo repartidor
    @Transactional
    public RepartidorDTO createRepartidor(RepartidorDTO repartidorDTO) {
        // Validar que el correo electrónico no exista
        if (repartidorRepository.existsByCorreo_Electronico(repartidorDTO.getCorreo_Electronico())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado para otro repartidor.");
        }
        // Validar que el teléfono no exista
        if (repartidorRepository.existsByTelefono(repartidorDTO.getTelefono())) {
            throw new IllegalArgumentException("El teléfono ya está registrado para otro repartidor.");
        }
        // Validar que la placa no exista
        if (repartidorRepository.existsByPlaca(repartidorDTO.getPlaca())) {
            throw new IllegalArgumentException("La placa del vehículo ya está registrada para otro repartidor.");
        }

        Repartidor repartidor = convertToEntity(repartidorDTO);
        // Guardamos la contraseña tal cual (TEMPORALMENTE sin hashing)
        repartidor.setContrasena(repartidorDTO.getContrasena());
        Repartidor savedRepartidor = repartidorRepository.save(repartidor);
        return convertToDTO(savedRepartidor);
    }

    // Lógica de negocio: Actualizar un repartidor existente
    @Transactional
    public Optional<RepartidorDTO> updateRepartidor(Integer id, RepartidorDTO repartidorDTO) {
        return repartidorRepository.findById(id).map(existingRepartidor -> {
            // Validar unicidad del correo electrónico si cambia
            if (!existingRepartidor.getCorreoElectronico().equals(repartidorDTO.getCorreo_Electronico()) &&
                repartidorRepository.existsByCorreo_Electronico(repartidorDTO.getCorreo_Electronico())) {
                throw new IllegalArgumentException("El nuevo correo electrónico ya está en uso por otro repartidor.");
            }
            // Validar unicidad del teléfono si cambia
            if (!existingRepartidor.getTelefono().equals(repartidorDTO.getTelefono()) &&
                repartidorRepository.existsByTelefono(repartidorDTO.getTelefono())) {
                throw new IllegalArgumentException("El nuevo teléfono ya está en uso por otro repartidor.");
            }
            // Validar unicidad de la placa si cambia
            if (!existingRepartidor.getPlaca().equals(repartidorDTO.getPlaca()) &&
                repartidorRepository.existsByPlaca(repartidorDTO.getPlaca())) {
                throw new IllegalArgumentException("La nueva placa del vehículo ya está en uso por otro repartidor.");
            }

            existingRepartidor.setNombre(repartidorDTO.getNombre());
            existingRepartidor.setApellido(repartidorDTO.getApellido());
            existingRepartidor.setCorreoElectronico(repartidorDTO.getCorreo_Electronico()); // Asumo que en la Entidad es camelCase, DTO es snake_case
            existingRepartidor.setTelefono(repartidorDTO.getTelefono());
            existingRepartidor.setPlaca(repartidorDTO.getPlaca());

            // Solo actualizar la contraseña si se proporciona una nueva
            if (repartidorDTO.getContrasena() != null && !repartidorDTO.getContrasena().isEmpty()) {
                existingRepartidor.setContrasena(repartidorDTO.getContrasena()); // Sin hashing temporalmente
            }

            Repartidor updatedRepartidor = repartidorRepository.save(existingRepartidor);
            return convertToDTO(updatedRepartidor);
        });
    }

    // Lógica de negocio: Eliminar un repartidor y sus datos relacionados
    @Transactional
    public boolean deleteRepartidor(Integer id) {
        if (repartidorRepository.existsById(id)) {
            // Lógica de eliminación en cascada en la capa de servicio

            // Eliminar Pedidos asignados a este repartidor
            // NOTA: Reafirmando la advertencia de que eliminar pedidos históricos puede no ser deseable.
            // Si la FK en Pedido a Repartidor es nullable, considera actualizar a NULL.
            // Si es NO-NULLABLE, el DELETE es necesario para mantener la integridad.
            pedidoRepository.deleteByRepartidor_IdRepartidor(id);

            // Eliminar registros de Doble_Verificacion donde este repartidor participó
            dobleVerificacionRepository.deleteByRepartidor_IdRepartidor(id);

            // Finalmente, eliminar el repartidor
            repartidorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Lógica de negocio: Autenticar repartidor
    public Optional<RepartidorDTO> authenticateRepartidor(String correoElectronico, String contrasena) {
        return repartidorRepository.findByCorreo_Electronico(correoElectronico) // Usar el método del repo
                .filter(repartidor -> contrasena.equals(repartidor.getContrasena())) // Comparación directa (TEMPORAL)
                .map(this::convertToDTO);
    }

    // --- Métodos de Conversión (Helper Methods) ---

    private RepartidorDTO convertToDTO(Repartidor repartidor) {
        RepartidorDTO dto = new RepartidorDTO();
        dto.setIdRepartidor(repartidor.getIdRepartidor());
        dto.setNombre(repartidor.getNombre());
        dto.setApellido(repartidor.getApellido());
        dto.setCorreo_Electronico(repartidor.getCorreoElectronico()); // Asumo entidad usa camelCase
        dto.setTelefono(repartidor.getTelefono());
        dto.setPlaca(repartidor.getPlaca());
        // NO EXPONER LA CONTRASEÑA EN EL DTO PARA USO EN API REST REAL.
        return dto;
    }

    private Repartidor convertToEntity(RepartidorDTO repartidorDTO) {
        Repartidor repartidor = new Repartidor();
        repartidor.setIdRepartidor(repartidorDTO.getIdRepartidor());
        repartidor.setNombre(repartidorDTO.getNombre());
        repartidor.setApellido(repartidorDTO.getApellido());
        repartidor.setCorreoElectronico(repartidorDTO.getCorreo_Electronico()); // Asumo entidad usa camelCase, DTO es snake_case
        repartidor.setTelefono(repartidorDTO.getTelefono());
        repartidor.setPlaca(repartidorDTO.getPlaca());
        repartidor.setContrasena(repartidorDTO.getContrasena());
        return repartidor;
    }
}