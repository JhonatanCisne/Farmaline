package com.farmaline.farmaline.service;

import java.math.BigDecimal;
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

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private LoteProductoService loteProductoService;

    public List<DetallePedidoDTO> getAllDetallesPedido() {
        return detallePedidoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<DetallePedidoDTO> getDetallePedidoById(Integer id) {
        return detallePedidoRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<DetallePedidoDTO> getDetallesByPedidoId(Integer idPedido) {
        return detallePedidoRepository.findByPedido_IdPedido(idPedido).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DetallePedidoDTO createDetallePedido(DetallePedidoDTO detallePedidoDTO) {
        Pedido pedido = pedidoRepository.findById(detallePedidoDTO.getIdPedido())
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + detallePedidoDTO.getIdPedido()));

        Producto producto = productoRepository.findById(detallePedidoDTO.getIdProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + detallePedidoDTO.getIdProducto()));

        int stockDisponibleTotal = loteProductoService.getTotalStockByProductId(producto.getIdProducto());
        if (stockDisponibleTotal < detallePedidoDTO.getCantidad()) {
            throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombre() + ". Solo quedan " + stockDisponibleTotal);
        }

        DetallePedido detallePedido = new DetallePedido();
        detallePedido.setPedido(pedido);
        detallePedido.setProducto(producto);
        detallePedido.setCantidad(detallePedidoDTO.getCantidad());
        detallePedido.setPrecioUnitarioAlMomentoCompra(producto.getPrecioFinal());
        detallePedido.setSubtotalDetalle(producto.getPrecioFinal().multiply(BigDecimal.valueOf(detallePedidoDTO.getCantidad())));

        loteProductoService.consumeStockByExpiration(producto.getIdProducto(), detallePedidoDTO.getCantidad());

        pedido.setMontoTotalPedido(pedido.getMontoTotalPedido().add(detallePedido.getSubtotalDetalle()));
        pedidoRepository.save(pedido);

        DetallePedido savedDetalle = detallePedidoRepository.save(detallePedido);
        return convertToDTO(savedDetalle);
    }

    @Transactional
    public Optional<DetallePedidoDTO> updateDetallePedido(Integer idDetallePedido, DetallePedidoDTO detallePedidoDTO) {
        return detallePedidoRepository.findById(idDetallePedido).map(existingDetalle -> {
            Pedido pedido = existingDetalle.getPedido();
            Producto producto = existingDetalle.getProducto();
            int oldCantidad = existingDetalle.getCantidad();
            BigDecimal oldSubtotal = existingDetalle.getSubtotalDetalle();

            if (detallePedidoDTO.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor que cero. Para eliminar un item, usa el método deleteDetallePedido.");
            }

            int newCantidad = detallePedidoDTO.getCantidad();
            int diferenciaCantidad = newCantidad - oldCantidad;

            if (diferenciaCantidad > 0) {
                int stockDisponibleTotal = loteProductoService.getTotalStockByProductId(producto.getIdProducto());
                if (stockDisponibleTotal < diferenciaCantidad) {
                    throw new IllegalStateException("Stock insuficiente para aumentar la cantidad del producto: " + producto.getNombre() + ". Solo quedan " + stockDisponibleTotal + " unidades adicionales.");
                }
                loteProductoService.consumeStockByExpiration(producto.getIdProducto(), diferenciaCantidad);
            }

            existingDetalle.setCantidad(newCantidad);
            existingDetalle.setPrecioUnitarioAlMomentoCompra(producto.getPrecioFinal());
            BigDecimal newSubtotal = producto.getPrecioFinal().multiply(BigDecimal.valueOf(newCantidad));
            existingDetalle.setSubtotalDetalle(newSubtotal);

            pedido.setMontoTotalPedido(pedido.getMontoTotalPedido().subtract(oldSubtotal).add(newSubtotal));
            pedidoRepository.save(pedido);

            DetallePedido updatedDetalle = detallePedidoRepository.save(existingDetalle);
            return convertToDTO(updatedDetalle);
        });
    }

    @Transactional
    public boolean deleteDetallePedido(Integer idDetallePedido) {
        return detallePedidoRepository.findById(idDetallePedido).map(detalle -> {
            Pedido pedido = detalle.getPedido();
            
            pedido.setMontoTotalPedido(pedido.getMontoTotalPedido().subtract(detalle.getSubtotalDetalle()));
            pedidoRepository.save(pedido);

            detallePedidoRepository.delete(detalle);
            return true;
        }).orElse(false);
    }

    private DetallePedidoDTO convertToDTO(DetallePedido detallePedido) {
        DetallePedidoDTO dto = new DetallePedidoDTO();
        dto.setIdDetallePedido(detallePedido.getIdDetallePedido());
        dto.setIdPedido(detallePedido.getPedido() != null ? detallePedido.getPedido().getIdPedido() : null);
        dto.setIdProducto(detallePedido.getProducto() != null ? detallePedido.getProducto().getIdProducto() : null);
        dto.setCantidad(detallePedido.getCantidad());
        dto.setPrecioUnitarioAlMomentoCompra(detallePedido.getPrecioUnitarioAlMomentoCompra());
        dto.setSubtotalDetalle(detallePedido.getSubtotalDetalle());
        return dto;
    }
}