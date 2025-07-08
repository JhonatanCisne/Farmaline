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
import com.farmaline.farmaline.model.Repartidor;
import com.farmaline.farmaline.model.Usuario;
import com.farmaline.farmaline.repository.CarritoAnadidoRepository;
import com.farmaline.farmaline.repository.CarritoRepository;
import com.farmaline.farmaline.repository.DetallePedidoRepository;
import com.farmaline.farmaline.repository.PedidoRepository;
import com.farmaline.farmaline.repository.RepartidorRepository;
import com.farmaline.farmaline.repository.UsuarioRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private CarritoAnadidoRepository carritoAnadidoRepository;
    @Autowired
    private DetallePedidoRepository detallePedidoRepository;
    @Autowired
    private RepartidorRepository repartidorRepository;
    @Autowired
    private DetallePedidoService detallePedidoService;

    private static final BigDecimal IGV_RATE = new BigDecimal("0.18");
    private static final String ESTADO_PEDIDO_PENDIENTE = "Pendiente";
    private static final String ESTADO_PEDIDO_COMPLETADO = "Completado";
    private static final String ESTADO_VERIFICACION_PENDIENTE = "Pendiente";
    private static final String ESTADO_VERIFICACION_CONFIRMADO = "Confirmado";

    public List<PedidoDTO> getAllPedidos() {
        return pedidoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PedidoDTO> getPedidosByUsuarioId(Integer idUsuario) {
        return pedidoRepository.findByUsuario_IdUsuario(idUsuario).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PedidoDTO> getPedidosByRepartidorId(Integer idRepartidor) {
        return pedidoRepository.findByRepartidor_IdRepartidor(idRepartidor).stream()
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

        Pedido newPedido = new Pedido();
        newPedido.setUsuario(usuario);
        newPedido.setFecha(LocalDate.now());
        newPedido.setHora(LocalTime.now());
        newPedido.setMontoTotalPedido(BigDecimal.ZERO);
        newPedido.setRepartidor(null);
        newPedido.setEstado(ESTADO_PEDIDO_PENDIENTE);
        newPedido.setEstadoUsuarioVerificacion(ESTADO_VERIFICACION_PENDIENTE);
        newPedido.setEstadoRepartidorVerificacion(ESTADO_VERIFICACION_PENDIENTE);

        Pedido savedPedido = pedidoRepository.save(newPedido);

        for (Carrito_Anadido item : itemsCarrito) {
            DetallePedidoDTO detalleDTO = new DetallePedidoDTO();
            detalleDTO.setIdPedido(savedPedido.getIdPedido());
            detalleDTO.setIdProducto(item.getProducto().getIdProducto());
            detalleDTO.setCantidad(item.getCantidad());
            detallePedidoService.createDetallePedido(detalleDTO);
        }

        Optional<Pedido> optionalReloadedPedido = pedidoRepository.findById(savedPedido.getIdPedido());
        if (!optionalReloadedPedido.isPresent()) {
            throw new IllegalStateException("El pedido recién creado no pudo ser recargado. ID: " + savedPedido.getIdPedido());
        }
        Pedido reloadedPedido = optionalReloadedPedido.get();

        BigDecimal subtotalPedido = detallePedidoRepository.findByPedido_IdPedido(reloadedPedido.getIdPedido())
                                                            .stream()
                                                            .map(DetallePedido::getSubtotalDetalle)
                                                            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal igv = subtotalPedido.multiply(IGV_RATE);
        BigDecimal montoFinalPedido = subtotalPedido.add(igv);

        reloadedPedido.setMontoTotalPedido(montoFinalPedido);
        pedidoRepository.save(reloadedPedido);

        carritoAnadidoRepository.deleteByCarrito_IdCarrito(idCarrito);

        return convertToDTO(reloadedPedido);
    }

    @Transactional
    public Optional<PedidoDTO> updatePedido(Integer idPedido, PedidoDTO pedidoDTO) {
        return pedidoRepository.findById(idPedido).map(existingPedido -> {
            existingPedido.setFecha(pedidoDTO.getFecha());
            existingPedido.setHora(pedidoDTO.getHora());
            
            if (pedidoDTO.getIdUsuario() != null) {
                usuarioRepository.findById(pedidoDTO.getIdUsuario())
                                        .ifPresent(existingPedido::setUsuario);
            }

            if (pedidoDTO.getIdRepartidor() != null) {
                repartidorRepository.findById(pedidoDTO.getIdRepartidor())
                                        .orElseThrow(() -> new IllegalArgumentException("Repartidor no encontrado con ID: " + pedidoDTO.getIdRepartidor()));
            } else {
                existingPedido.setRepartidor(null);
            }

            if (pedidoDTO.getEstadoPedido() != null && !pedidoDTO.getEstadoPedido().isEmpty()) {
                existingPedido.setEstado(pedidoDTO.getEstadoPedido());
            }
            
            if (pedidoDTO.getEstadoUsuarioVerificacion() != null && !pedidoDTO.getEstadoUsuarioVerificacion().isEmpty()) {
                existingPedido.setEstadoUsuarioVerificacion(pedidoDTO.getEstadoUsuarioVerificacion());
            }
            if (pedidoDTO.getEstadoRepartidorVerificacion() != null && !pedidoDTO.getEstadoRepartidorVerificacion().isEmpty()) {
                existingPedido.setEstadoRepartidorVerificacion(pedidoDTO.getEstadoRepartidorVerificacion());
            }

            Pedido updatedPedido = pedidoRepository.save(existingPedido);
            Optional<Pedido> reloadedUpdatedPedido = pedidoRepository.findById(updatedPedido.getIdPedido());
            return reloadedUpdatedPedido.map(this::convertToDTO).orElse(null);
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
            Optional<Pedido> reloadedUpdatedPedido = pedidoRepository.findById(updatedPedido.getIdPedido());
            return reloadedUpdatedPedido.map(this::convertToDTO).orElse(null);
        });
    }

    @Transactional
    public boolean deletePedido(Integer id) {
        if (pedidoRepository.existsById(id)) {
            detallePedidoRepository.deleteByPedido_IdPedido(id);
            pedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public Optional<PedidoDTO> confirmarEntregaUsuario(Integer idPedido) {
        return pedidoRepository.findById(idPedido).map(pedido -> {
            if (ESTADO_PEDIDO_COMPLETADO.equals(pedido.getEstado())) {
                throw new IllegalStateException("El pedido ya está completado.");
            }
            if (ESTADO_VERIFICACION_CONFIRMADO.equals(pedido.getEstadoUsuarioVerificacion())) {
                throw new IllegalStateException("La entrega ya ha sido confirmada por el usuario para este pedido.");
            }

            pedido.setEstadoUsuarioVerificacion(ESTADO_VERIFICACION_CONFIRMADO);
            
            if (ESTADO_VERIFICACION_CONFIRMADO.equals(pedido.getEstadoRepartidorVerificacion())) {
                pedido.setEstado(ESTADO_PEDIDO_COMPLETADO);
            }
            
            Pedido updatedPedido = pedidoRepository.save(pedido);
            Optional<Pedido> reloadedUpdatedPedido = pedidoRepository.findById(updatedPedido.getIdPedido());
            return reloadedUpdatedPedido.map(this::convertToDTO).orElse(null);
        });
    }

    @Transactional
    public Optional<PedidoDTO> confirmarPagoRepartidor(Integer idPedido) {
        return pedidoRepository.findById(idPedido).map(pedido -> {
            if (ESTADO_PEDIDO_COMPLETADO.equals(pedido.getEstado())) {
                throw new IllegalStateException("El pedido ya está completado.");
            }
            if (ESTADO_VERIFICACION_CONFIRMADO.equals(pedido.getEstadoRepartidorVerificacion())) {
                throw new IllegalStateException("El pago ya ha sido confirmado por el repartidor para este pedido.");
            }

            pedido.setEstadoRepartidorVerificacion(ESTADO_VERIFICACION_CONFIRMADO);

            if (ESTADO_VERIFICACION_CONFIRMADO.equals(pedido.getEstadoUsuarioVerificacion())) {
                pedido.setEstado(ESTADO_PEDIDO_COMPLETADO);
            }

            Pedido updatedPedido = pedidoRepository.save(pedido);
            Optional<Pedido> reloadedUpdatedPedido = pedidoRepository.findById(updatedPedido.getIdPedido());
            return reloadedUpdatedPedido.map(this::convertToDTO).orElse(null);
        });
    }

    public List<PedidoDTO> getPedidosByEstado(String estado) {
        return pedidoRepository.findByEstado(estado).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PedidoDTO> getPedidosPendientes() {
        return getPedidosByEstado(ESTADO_PEDIDO_PENDIENTE);
    }

    public List<PedidoDTO> getPedidosRealizados() {
        return getPedidosByEstado(ESTADO_PEDIDO_COMPLETADO);
    }

    public List<PedidoDTO> getPedidosPendientesDobleVerificacionByRepartidor(Integer idRepartidor) {
        return pedidoRepository.findByRepartidor_IdRepartidorAndEstadoRepartidorVerificacion(idRepartidor, ESTADO_VERIFICACION_PENDIENTE).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PedidoDTO> getPedidosEnCaminoByRepartidor(Integer idRepartidor) {
        return pedidoRepository.findByRepartidor_IdRepartidorAndEstado(idRepartidor, "En Camino").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PedidoDTO convertToDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFecha(pedido.getFecha());
        dto.setHora(pedido.getHora());
        dto.setMontoTotalPedido(pedido.getMontoTotalPedido());
        dto.setIdUsuario(pedido.getUsuario() != null ? pedido.getUsuario().getIdUsuario() : null);
        dto.setIdRepartidor(pedido.getRepartidor() != null ? pedido.getRepartidor().getIdRepartidor() : null);
        dto.setEstadoPedido(pedido.getEstado());
        dto.setEstadoUsuarioVerificacion(pedido.getEstadoUsuarioVerificacion());
        dto.setEstadoRepartidorVerificacion(pedido.getEstadoRepartidorVerificacion());

        List<DetallePedidoDTO> detallesDTO = pedido.getDetallesPedido() != null ?
            pedido.getDetallesPedido().stream()
                    .map(this::convertDetalleToDTO)
                    .collect(Collectors.toList()) :
            List.of();
            
        dto.setDetallesPedido(detallesDTO);
        
        if (pedido.getUsuario() != null) {
            dto.setNombreUsuario(pedido.getUsuario().getNombre());
            dto.setDomicilioUsuario(pedido.getUsuario().getDomicilio());
        } else if (pedido.getUsuario() != null && pedido.getUsuario().getIdUsuario() != null) {
            usuarioRepository.findById(pedido.getUsuario().getIdUsuario()).ifPresent(usuario -> {
                dto.setNombreUsuario(usuario.getNombre());
                dto.setDomicilioUsuario(usuario.getDomicilio());
            });
        } else {
            dto.setNombreUsuario("Usuario Desconocido");
            dto.setDomicilioUsuario("Dirección Desconocida");
        }

        return dto;
    }

    private DetallePedidoDTO convertDetalleToDTO(DetallePedido detalle) {
        DetallePedidoDTO dto = new DetallePedidoDTO();
        dto.setIdDetallePedido(detalle.getIdDetallePedido());
        dto.setIdPedido(detalle.getPedido() != null ? detalle.getPedido().getIdPedido() : null);
        dto.setIdProducto(detalle.getProducto() != null ? detalle.getProducto().getIdProducto() : null);
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitarioAlMomentoCompra(detalle.getPrecioUnitarioAlMomentoCompra());
        dto.setSubtotalDetalle(detalle.getSubtotalDetalle());
        
        if (detalle.getProducto() != null) {
            dto.setNombreProducto(detalle.getProducto().getNombre()); 
        } else {
            dto.setNombreProducto("Producto Desconocido"); 
        }
        return dto;
    }
}