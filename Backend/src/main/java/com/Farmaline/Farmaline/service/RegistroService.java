package com.Farmaline.farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.dto.RegistroDTO;
import com.farmaline.farmaline.model.Doble_Verificacion;
import com.farmaline.farmaline.model.Pedido;
import com.farmaline.farmaline.model.Registro;
import com.farmaline.farmaline.repository.DobleVerificacionRepository;
import com.farmaline.farmaline.repository.PedidoRepository;
import com.farmaline.farmaline.repository.RegistroRepository;

@Service
public class RegistroService {

    private final RegistroRepository registroRepository;
    private final PedidoRepository pedidoRepository;
    private final DobleVerificacionRepository dobleVerificacionRepository;

    @Autowired
    public RegistroService(RegistroRepository registroRepository, PedidoRepository pedidoRepository, DobleVerificacionRepository dobleVerificacionRepository) {
        this.registroRepository = registroRepository;
        this.pedidoRepository = pedidoRepository;
        this.dobleVerificacionRepository = dobleVerificacionRepository;
    }

    public List<RegistroDTO> obtenerTodosRegistros() {
        return registroRepository.findAll().stream()
                .map(this::convertirARegistroDTO)
                .collect(Collectors.toList());
    }

    public Optional<RegistroDTO> obtenerRegistroPorId(Integer id) {
        return registroRepository.findById(id)
                .map(this::convertirARegistroDTO);
    }

    @Transactional
    public RegistroDTO crearRegistro(RegistroDTO registroDTO) {
        Registro registro = new Registro();

        Pedido pedido = pedidoRepository.findById(registroDTO.getIdPedido())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        registro.setPedido(pedido);

        Doble_Verificacion dobleVerificacion = dobleVerificacionRepository.findById(registroDTO.getIdDobleVerificacion())
                .orElseThrow(() -> new RuntimeException("Doble Verificación no encontrada"));
        registro.setDobleVerificacion(dobleVerificacion);

        registro = registroRepository.save(registro);
        return convertirARegistroDTO(registro);
    }

    @Transactional
    public Optional<RegistroDTO> actualizarRegistro(Integer id, RegistroDTO registroDTO) {
        return registroRepository.findById(id)
                .map(registroExistente -> {
                    if (registroDTO.getIdPedido() != null) {
                        Pedido pedido = pedidoRepository.findById(registroDTO.getIdPedido())
                                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
                        registroExistente.setPedido(pedido);
                    }

                    if (registroDTO.getIdDobleVerificacion() != null) {
                        Doble_Verificacion dobleVerificacion = dobleVerificacionRepository.findById(registroDTO.getIdDobleVerificacion())
                                .orElseThrow(() -> new RuntimeException("Doble Verificación no encontrada"));
                        registroExistente.setDobleVerificacion(dobleVerificacion);
                    }
                    return convertirARegistroDTO(registroRepository.save(registroExistente));
                });
    }

    public boolean eliminarRegistro(Integer id) {
        if (registroRepository.existsById(id)) {
            registroRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private RegistroDTO convertirARegistroDTO(Registro registro) {
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
}