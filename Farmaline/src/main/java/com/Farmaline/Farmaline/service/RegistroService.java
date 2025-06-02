package com.Farmaline.Farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Farmaline.Farmaline.dto.RegistroDTO;
import com.Farmaline.Farmaline.model.Doble_Verificacion;
import com.Farmaline.Farmaline.model.Pedido;
import com.Farmaline.Farmaline.model.Registro;
import com.Farmaline.Farmaline.repository.Doble_VerificacionRepository;
import com.Farmaline.Farmaline.repository.PedidoRepository;
import com.Farmaline.Farmaline.repository.RegistroRepository;

@Service
public class RegistroService {

    private final RegistroRepository registroRepository;
    private final PedidoRepository pedidoRepository;
    private final Doble_VerificacionRepository dobleVerificacionRepository;

    @Autowired
    public RegistroService(
            RegistroRepository registroRepository,
            PedidoRepository pedidoRepository,
            Doble_VerificacionRepository dobleVerificacionRepository) {
        this.registroRepository = registroRepository;
        this.pedidoRepository = pedidoRepository;
        this.dobleVerificacionRepository = dobleVerificacionRepository;
    }

    private RegistroDTO convertToDto(Registro registro) {
        if (registro == null) {
            return null;
        }
        RegistroDTO dto = new RegistroDTO();
        dto.setIdRegistro(registro.getIdRegistro());
        if (registro.getPedido() != null) {
            dto.setIdPedido(registro.getPedido().getIdPedido());
        }
        if (registro.getDobleVerificacion() != null) {
            dto.setIdDobleVerificacion(registro.getDobleVerificacion().getIdDobleVerifiacion());
        }
        return dto;
    }

    private Registro convertToEntity(RegistroDTO dto) {
        if (dto == null) {
            return null;
        }
        Registro registro = new Registro();

        if (dto.getIdPedido() != null) {
            Pedido pedido = pedidoRepository.findById(dto.getIdPedido())
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + dto.getIdPedido()));
            registro.setPedido(pedido);
        } else {
            throw new IllegalArgumentException("ID de pedido es obligatorio para el registro.");
        }

        if (dto.getIdDobleVerificacion() != null) {
            Doble_Verificacion dobleVerificacion = dobleVerificacionRepository.findById(dto.getIdDobleVerificacion())
                    .orElseThrow(() -> new RuntimeException("Doble Verificación no encontrada con ID: " + dto.getIdDobleVerificacion()));
            registro.setDobleVerificacion(dobleVerificacion);
        } else {
            throw new IllegalArgumentException("ID de Doble Verificación es obligatorio para el registro.");
        }
        return registro;
    }

    @Transactional(readOnly = true)
    public List<RegistroDTO> findAllRegistros() {
        return registroRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<RegistroDTO> findRegistroById(Integer id) {
        return registroRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Optional<RegistroDTO> findRegistroByPedidoId(Integer pedidoId) {
        return registroRepository.findByPedidoIdPedido(pedidoId)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Optional<RegistroDTO> findRegistroByDobleVerificacionId(Integer dobleVerificacionId) {
        return registroRepository.findByDobleVerificacionIdDobleVerifiacion(dobleVerificacionId)
                .map(this::convertToDto);
    }

    @Transactional
    public RegistroDTO createRegistro(RegistroDTO registroDTO) {
        if (registroRepository.findByPedidoIdPedido(registroDTO.getIdPedido()).isPresent()) {
            throw new IllegalStateException("El pedido con ID " + registroDTO.getIdPedido() + " ya tiene un registro asociado.");
        }
        if (registroRepository.findByDobleVerificacionIdDobleVerifiacion(registroDTO.getIdDobleVerificacion()).isPresent()) {
            throw new IllegalStateException("La doble verificación con ID " + registroDTO.getIdDobleVerificacion() + " ya tiene un registro asociado.");
        }

        Registro registro = convertToEntity(registroDTO);
        Registro savedRegistro = registroRepository.save(registro);
        return convertToDto(savedRegistro);
    }

    @Transactional
    public void deleteRegistro(Integer id) {
        if (!registroRepository.existsById(id)) {
            throw new RuntimeException("Registro con ID " + id + " no encontrado para eliminar.");
        }
        registroRepository.deleteById(id);
    }
}