package com.farmaline.farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.dto.RegistroDTO;
import com.farmaline.farmaline.model.Pedido;
import com.farmaline.farmaline.model.Registro;
import com.farmaline.farmaline.repository.PedidoRepository;
import com.farmaline.farmaline.repository.RegistroRepository;

@Service
public class RegistroService {

    @Autowired
    private RegistroRepository registroRepository;
    @Autowired
    private PedidoRepository pedidoRepository;

    public List<RegistroDTO> getAllRegistros() {
        return registroRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<RegistroDTO> getRegistroById(Integer id) {
        return registroRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<RegistroDTO> getRegistroByPedidoId(Integer idPedido) {
        return registroRepository.findByPedido_IdPedido(idPedido)
                .map(this::convertToDTO);
    }

    @Transactional
    public RegistroDTO createRegistro(RegistroDTO registroDTO) {
        Pedido pedido = pedidoRepository.findById(registroDTO.getIdPedido())
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + registroDTO.getIdPedido()));

        if (registroRepository.existsByPedido_IdPedido(registroDTO.getIdPedido())) {
            throw new IllegalStateException("Ya existe un registro para el Pedido con ID: " + registroDTO.getIdPedido());
        }

        Registro nuevoRegistro = new Registro();
        nuevoRegistro.setPedido(pedido);

        Registro savedRegistro = registroRepository.save(nuevoRegistro);
        return convertToDTO(savedRegistro);
    }

    @Transactional
    public boolean deleteRegistro(Integer id) {
        if (registroRepository.existsById(id)) {
            registroRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteRegistroByPedidoId(Integer idPedido) {
        if (registroRepository.existsByPedido_IdPedido(idPedido)) {
            registroRepository.deleteByPedido_IdPedido(idPedido);
            return true;
        }
        return false;
    }

    private RegistroDTO convertToDTO(Registro registro) {
        RegistroDTO dto = new RegistroDTO();
        dto.setIdRegistro(registro.getIdRegistro());
        dto.setIdPedido(registro.getPedido() != null ? registro.getPedido().getIdPedido() : null);
        return dto;
    }
}