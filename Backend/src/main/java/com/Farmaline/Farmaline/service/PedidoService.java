package com.farmaline.farmaline.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired; // Importar HashSet para inicializar Set
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
        newPedido.setEstado(ESTADO_PEDIDO_PENDIENTE); // Estado inicial del pedido
        newPedido.setEstadoUsuarioVerificacion(ESTADO_VERIFICACION_PENDIENTE); // Estado inicial de verificación usuario
        newPedido.setEstadoRepartidorVerificacion(ESTADO_VERIFICACION_PENDIENTE); // Estado inicial de verificación repartidor

        Pedido savedPedido = pedidoRepository.save(newPedido); // Guarda el pedido inicial

        for (Carrito_Anadido item : itemsCarrito) {
            DetallePedidoDTO detalleDTO = new DetallePedidoDTO();
            detalleDTO.setIdPedido(savedPedido.getIdPedido()); // Importante para asociar
            detalleDTO.setIdProducto(item.getProducto().getIdProducto());
            detalleDTO.setCantidad(item.getCantidad());
            detallePedidoService.createDetallePedido(detalleDTO);
        }

        // AHORA: Recarga el pedido para asegurar que la colección detallesPedido esté cargada
        // Esto es crucial porque los detalles fueron creados por el detallePedidoService
        // y no se añadieron directamente a la instancia 'savedPedido' en memoria.
        Optional<Pedido> optionalReloadedPedido = pedidoRepository.findById(savedPedido.getIdPedido());
        if (!optionalReloadedPedido.isPresent()) {
            throw new IllegalStateException("El pedido recién creado no pudo ser recargado. ID: " + savedPedido.getIdPedido());
        }
        Pedido reloadedPedido = optionalReloadedPedido.get();


        // Calcula el subtotal del pedido usando los detalles recién creados (que ahora JPA puede cargar)
        BigDecimal subtotalPedido = detallePedidoRepository.findByPedido_IdPedido(reloadedPedido.getIdPedido())
                                                            .stream()
                                                            .map(DetallePedido::getSubtotalDetalle)
                                                            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal igv = subtotalPedido.multiply(IGV_RATE);
        BigDecimal montoFinalPedido = subtotalPedido.add(igv);

        reloadedPedido.setMontoTotalPedido(montoFinalPedido); // Actualiza el monto en el pedido recargado
        pedidoRepository.save(reloadedPedido); // Guarda el pedido recargado con el monto total

        carritoAnadidoRepository.deleteByCarrito_IdCarrito(idCarrito);

        return convertToDTO(reloadedPedido); // Convierte el pedido recargado (que tiene los detalles cargados)
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
            
            // Actualizar estados de verificación si vienen en el DTO
            if (pedidoDTO.getEstadoUsuarioVerificacion() != null && !pedidoDTO.getEstadoUsuarioVerificacion().isEmpty()) {
                existingPedido.setEstadoUsuarioVerificacion(pedidoDTO.getEstadoUsuarioVerificacion());
            }
            if (pedidoDTO.getEstadoRepartidorVerificacion() != null && !pedidoDTO.getEstadoRepartidorVerificacion().isEmpty()) {
                existingPedido.setEstadoRepartidorVerificacion(pedidoDTO.getEstadoRepartidorVerificacion());
            }

            Pedido updatedPedido = pedidoRepository.save(existingPedido);
            // IMPORTANTE: Recargar el pedido después de actualizar si sus relaciones (como detallesPedido)
            // pueden haber sido afectadas o si necesitas que estén frescas para el DTO.
            Optional<Pedido> reloadedUpdatedPedido = pedidoRepository.findById(updatedPedido.getIdPedido());
            return reloadedUpdatedPedido.map(this::convertToDTO).orElse(null); // Manejo de Optional
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
            // Recargar para asegurar que todas las relaciones estén actualizadas en el DTO
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

    // *** NUEVOS MÉTODOS para la doble verificación ***

    @Transactional
    public Optional<PedidoDTO> confirmarEntregaUsuario(Integer idPedido) {
        return pedidoRepository.findById(idPedido).map(pedido -> {
            // Validaciones
            if (ESTADO_PEDIDO_COMPLETADO.equals(pedido.getEstado())) {
                throw new IllegalStateException("El pedido ya está completado.");
            }
            if (ESTADO_VERIFICACION_CONFIRMADO.equals(pedido.getEstadoUsuarioVerificacion())) {
                throw new IllegalStateException("La entrega ya ha sido confirmada por el usuario para este pedido.");
            }

            pedido.setEstadoUsuarioVerificacion(ESTADO_VERIFICACION_CONFIRMADO);
            
            // Verificar si ambas partes han confirmado para cambiar el estado general del pedido
            if (ESTADO_VERIFICACION_CONFIRMADO.equals(pedido.getEstadoRepartidorVerificacion())) {
                pedido.setEstado(ESTADO_PEDIDO_COMPLETADO);
            } else {
                // Si el repartidor aún no confirma, el estado del pedido puede reflejar solo la confirmación del usuario
                // Para este ejemplo, solo se mantendrá en "Pendiente" hasta ambas confirmaciones.
            }
            
            Pedido updatedPedido = pedidoRepository.save(pedido);
            // Recargar para asegurar que todas las relaciones estén actualizadas en el DTO
            Optional<Pedido> reloadedUpdatedPedido = pedidoRepository.findById(updatedPedido.getIdPedido());
            return reloadedUpdatedPedido.map(this::convertToDTO).orElse(null);
        });
    }

    @Transactional
    public Optional<PedidoDTO> confirmarPagoRepartidor(Integer idPedido) {
        return pedidoRepository.findById(idPedido).map(pedido -> {
            // Validaciones
            if (ESTADO_PEDIDO_COMPLETADO.equals(pedido.getEstado())) {
                throw new IllegalStateException("El pedido ya está completado.");
            }
            if (ESTADO_VERIFICACION_CONFIRMADO.equals(pedido.getEstadoRepartidorVerificacion())) {
                throw new IllegalStateException("El pago ya ha sido confirmado por el repartidor para este pedido.");
            }

            pedido.setEstadoRepartidorVerificacion(ESTADO_VERIFICACION_CONFIRMADO);

            // Verificar si ambas partes han confirmado para cambiar el estado general del pedido
            if (ESTADO_VERIFICACION_CONFIRMADO.equals(pedido.getEstadoUsuarioVerificacion())) {
                pedido.setEstado(ESTADO_PEDIDO_COMPLETADO);
            } else {
                // Si el usuario aún no confirma, el estado del pedido puede reflejar solo la confirmación del repartidor
                // Para este ejemplo, solo se mantendrá en "Pendiente" hasta ambas confirmaciones.
            }

            Pedido updatedPedido = pedidoRepository.save(pedido);
            // Recargar para asegurar que todas las relaciones estén actualizadas en el DTO
            Optional<Pedido> reloadedUpdatedPedido = pedidoRepository.findById(updatedPedido.getIdPedido());
            return reloadedUpdatedPedido.map(this::convertToDTO).orElse(null);
        });
    }

    // *** NUEVOS MÉTODOS DE BÚSQUEDA POR ESTADO GENERAL ***

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


    // *** MÉTODOS DE CONVERSIÓN DTO ***

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

        // Manejo de null para detallesPedido en caso de que aún no esté inicializado
        List<DetallePedidoDTO> detallesDTO = pedido.getDetallesPedido() != null ? 
            pedido.getDetallesPedido().stream()
                  .map(this::convertDetalleToDTO)
                  .collect(Collectors.toList()) : 
            List.of(); // Devuelve una lista vacía si es null
            
        dto.setDetallesPedido(detallesDTO);
        return dto;
    }

    private DetallePedidoDTO convertDetalleToDTO(DetallePedido detalle) {
       DetallePedidoDTO dto = new DetallePedidoDTO();
       dto.setIdDetallePedido(detalle.getIdDetallePedido());
       dto.setIdPedido(detalle.getPedido() != null ? detalle.getPedido().getIdPedido() : null); // Manejo de null
       dto.setIdProducto(detalle.getProducto() != null ? detalle.getProducto().getIdProducto() : null); // Manejo de null
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