package com.farmaline.farmaline.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.dto.DetallePedidoDTO;
import com.farmaline.farmaline.dto.PedidoDTO;
import com.farmaline.farmaline.model.Carrito_Anadido;
import com.farmaline.farmaline.model.DetallePedido;
import com.farmaline.farmaline.model.Pedido;
import com.farmaline.farmaline.model.Producto;
import com.farmaline.farmaline.model.Repartidor;
import com.farmaline.farmaline.model.Usuario;
import com.farmaline.farmaline.repository.CarritoAnadidoRepository;
import com.farmaline.farmaline.repository.CarritoRepository;
import com.farmaline.farmaline.repository.DetallePedidoRepository;
import com.farmaline.farmaline.repository.PedidoRepository;
import com.farmaline.farmaline.repository.ProductoRepository;
import com.farmaline.farmaline.repository.RepartidorRepository;
import com.farmaline.farmaline.repository.UsuarioRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private CarritoAnadidoRepository carritoAnadidoRepository;
    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private RepartidorRepository repartidorRepository;

    public List<PedidoDTO> getAllPedidos() {
        return pedidoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<PedidoDTO> getPedidoById(Integer id) {
        return pedidoRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional
    public PedidoDTO createPedidoFromCarrito(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));

        Integer idCarrito = carritoRepository.findByUsuario_IdUsuario(idUsuario)
                                            .map(carrito -> carrito.getIdCarrito())
                                            .orElseThrow(() -> new IllegalStateException("Carrito no encontrado para el usuario: " + idUsuario));

        List<Carrito_Anadido> itemsCarrito = carritoAnadidoRepository.findByCarrito_IdCarrito(idCarrito);
        if (itemsCarrito.isEmpty()) {
            throw new IllegalArgumentException("El carrito del usuario está vacío. No se puede crear un pedido.");
        }

        BigDecimal montoTotalPedido = BigDecimal.ZERO;
        for (Carrito_Anadido item : itemsCarrito) {
            Producto producto = item.getProducto();
            if (producto.getStockDisponible() < item.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombre() + ". Solo quedan " + producto.getStockDisponible());
            }
            montoTotalPedido = montoTotalPedido.add(producto.getPrecioFinal().multiply(BigDecimal.valueOf(item.getCantidad())));
        }

        Pedido newPedido = new Pedido();
        newPedido.setUsuario(usuario);
        newPedido.setFecha(LocalDate.now());
        newPedido.setHora(LocalTime.now());
        newPedido.setMontoTotalPedido(montoTotalPedido);
        newPedido.setRepartidor(null);

        Pedido savedPedido = pedidoRepository.save(newPedido);

        for (Carrito_Anadido item : itemsCarrito) {
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(savedPedido);
            detalle.setProducto(item.getProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitarioAlMomentoCompra(item.getProducto().getPrecioFinal());
            detalle.setSubtotalDetalle(item.getProducto().getPrecioFinal().multiply(BigDecimal.valueOf(item.getCantidad())));
            detallePedidoRepository.save(detalle);

            productoRepository.findById(item.getProducto().getIdProducto()).ifPresent(p -> {
                p.setStockDisponible(p.getStockDisponible() - item.getCantidad());
                productoRepository.save(p);
            });
        }

        carritoAnadidoRepository.deleteByCarrito_IdCarrito(idCarrito);

        return convertToDTO(savedPedido);
    }

    @Transactional
    public Optional<PedidoDTO> updatePedido(Integer idPedido, PedidoDTO pedidoDTO) {
        return pedidoRepository.findById(idPedido).map(existingPedido -> {
            existingPedido.setFecha(pedidoDTO.getFecha());
            existingPedido.setHora(pedidoDTO.getHora());
            existingPedido.setMontoTotalPedido(pedidoDTO.getMontoTotalPedido());
            
            if (pedidoDTO.getIdUsuario() != null) {
                usuarioRepository.findById(pedidoDTO.getIdUsuario())
                        .ifPresent(existingPedido::setUsuario);
            }

            if (pedidoDTO.getIdRepartidor() != null) {
                repartidorRepository.findById(pedidoDTO.getIdRepartidor())
                        .ifPresent(existingPedido::setRepartidor);
            } else {
                existingPedido.setRepartidor(null);
            }

            Pedido updatedPedido = pedidoRepository.save(existingPedido);
            return convertToDTO(updatedPedido);
        });
    }

    @Transactional
    public Optional<PedidoDTO> assignRepartidorToPedido(Integer idPedido, Integer idRepartidor) {
        return pedidoRepository.findById(idPedido).map(pedido -> {
            Repartidor repartidor = null;
            if (idRepartidor != null) {
                repartidor = repartidorRepository.findById(idRepartidor)
                                .orElseThrow(() -> new IllegalArgumentException("Repartidor no encontrado con ID: " + idRepartidor));
            }
            pedido.setRepartidor(repartidor);
            Pedido updatedPedido = pedidoRepository.save(pedido);
            return convertToDTO(updatedPedido);
        });
    }

    @Transactional
    public boolean deletePedido(Integer id) {
        if (pedidoRepository.existsById(id)) {

            List<DetallePedido> detalles = detallePedidoRepository.findByPedido_IdPedido(id);
            for (DetallePedido dp : detalles) {
                productoRepository.findById(dp.getProducto().getIdProducto()).ifPresent(p -> {
                    p.setStockDisponible(p.getStockDisponible() + dp.getCantidad());
                    productoRepository.save(p);
                });
            }
            
            detallePedidoRepository.deleteByPedido_IdPedido(id);
            
            pedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private PedidoDTO convertToDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFecha(pedido.getFecha());
        dto.setHora(pedido.getHora());
        dto.setMontoTotalPedido(pedido.getMontoTotalPedido());
        dto.setIdUsuario(pedido.getUsuario() != null ? pedido.getUsuario().getIdUsuario() : null);
        dto.setIdRepartidor(pedido.getRepartidor() != null ? pedido.getRepartidor().getIdRepartidor() : null);

        List<DetallePedidoDTO> detallesDTO = detallePedidoRepository.findByPedido_IdPedido(pedido.getIdPedido())
                                            .stream()
                                            .map(this::convertDetalleToDTO)
                                            .collect(Collectors.toList());
        dto.setDetallesPedido(detallesDTO);
        return dto;
    }

    private DetallePedidoDTO convertDetalleToDTO(DetallePedido detalle) {
       DetallePedidoDTO dto = new DetallePedidoDTO();
       dto.setIdDetallePedido(detalle.getIdDetallePedido());
       dto.setIdPedido(detalle.getPedido().getIdPedido());
       dto.setIdProducto(detalle.getProducto().getIdProducto());
       dto.setCantidad(detalle.getCantidad());
       dto.setPrecioUnitarioAlMomentoCompra(detalle.getPrecioUnitarioAlMomentoCompra());
       dto.setSubtotalDetalle(detalle.getSubtotalDetalle());
       return dto;
    }
}