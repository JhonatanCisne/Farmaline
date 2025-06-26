package com.farmaline.farmaline.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.dto.DetallePedidoDTO;
import com.farmaline.farmaline.model.DetallePedido;
import com.farmaline.farmaline.model.Pedido;
import com.farmaline.farmaline.model.Producto;
import com.farmaline.farmaline.repository.DetallePedidoRepository;
import com.farmaline.farmaline.repository.PedidoRepository;
import com.farmaline.farmaline.repository.ProductoRepository;

@Service
public class DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public DetallePedidoService(DetallePedidoRepository detallePedidoRepository, PedidoRepository pedidoRepository, ProductoRepository productoRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }

    public List<DetallePedidoDTO> obtenerTodosDetallesPedido() {
        return detallePedidoRepository.findAll().stream()
                .map(this::convertirADetallePedidoDTO)
                .collect(Collectors.toList());
    }

    public Optional<DetallePedidoDTO> obtenerDetallePedidoPorId(Integer id) {
        return detallePedidoRepository.findById(id)
                .map(this::convertirADetallePedidoDTO);
    }

    public List<DetallePedidoDTO> obtenerDetallesPorIdPedido(Integer idPedido) {
        return detallePedidoRepository.findByPedidoIdPedido(idPedido).stream()
                .map(this::convertirADetallePedidoDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DetallePedidoDTO crearDetallePedido(DetallePedidoDTO detallePedidoDTO) {
        DetallePedido detallePedido = new DetallePedido();
        detallePedido.setCantidad(detallePedidoDTO.getCantidad());
        detallePedido.setPrecioUnitarioAlMomentoCompra(detallePedidoDTO.getPrecioUnitarioAlMomentoCompra());

        Pedido pedido = pedidoRepository.findById(detallePedidoDTO.getIdPedido())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        detallePedido.setPedido(pedido);

        Producto producto = productoRepository.findById(detallePedidoDTO.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        detallePedido.setProducto(producto);

        detallePedido = detallePedidoRepository.save(detallePedido);
        return convertirADetallePedidoDTO(detallePedido);
    }

    @Transactional
    public Optional<DetallePedidoDTO> actualizarDetallePedido(Integer id, DetallePedidoDTO detallePedidoDTO) {
        return detallePedidoRepository.findById(id)
                .map(detalleExistente -> {
                    detalleExistente.setCantidad(detallePedidoDTO.getCantidad());
                    detalleExistente.setPrecioUnitarioAlMomentoCompra(detallePedidoDTO.getPrecioUnitarioAlMomentoCompra());

                    if (detallePedidoDTO.getIdPedido() != null) {
                        Pedido pedido = pedidoRepository.findById(detallePedidoDTO.getIdPedido())
                                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
                        detalleExistente.setPedido(pedido);
                    }

                    if (detallePedidoDTO.getIdProducto() != null) {
                        Producto producto = productoRepository.findById(detallePedidoDTO.getIdProducto())
                                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                        detalleExistente.setProducto(producto);
                    }
                    return convertirADetallePedidoDTO(detallePedidoRepository.save(detalleExistente));
                });
    }

    public boolean eliminarDetallePedido(Integer id) {
        if (detallePedidoRepository.existsById(id)) {
            detallePedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private DetallePedidoDTO convertirADetallePedidoDTO(DetallePedido detallePedido) {
        DetallePedidoDTO dto = new DetallePedidoDTO();
        dto.setIdDetallePedido(detallePedido.getIdDetallePedido());
        dto.setCantidad(detallePedido.getCantidad());
        dto.setPrecioUnitarioAlMomentoCompra(detallePedido.getPrecioUnitarioAlMomentoCompra());
        if (detallePedido.getPedido() != null) {
            dto.setIdPedido(detallePedido.getPedido().getIdPedido());
        }
        if (detallePedido.getProducto() != null) {
            dto.setIdProducto(detallePedido.getProducto().getIdProducto());
            dto.setNombreProducto(detallePedido.getProducto().getNombre());
        }
        return dto;
    }
}