package com.Farmaline.farmaline.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaline.farmaline.dto.CarritoAnadidoDTO;
import com.farmaline.farmaline.dto.DetallePedidoDTO;
import com.farmaline.farmaline.dto.PedidoDTO;
import com.farmaline.farmaline.model.Pedido;
import com.farmaline.farmaline.model.Producto;
import com.farmaline.farmaline.model.Usuario;
import com.farmaline.farmaline.repository.PedidoRepository;
import com.farmaline.farmaline.repository.ProductoRepository;
import com.farmaline.farmaline.repository.RepartidorRepository;
import com.farmaline.farmaline.repository.UsuarioRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RepartidorRepository repartidorRepository;
    private final DetallePedidoService detallePedidoService;
    private final CarritoAnadidoService carritoAnadidoService;
    private final ProductoRepository productoRepository;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository,
                         UsuarioRepository usuarioRepository,
                         RepartidorRepository repartidorRepository,
                         DetallePedidoService detallePedidoService,
                         CarritoAnadidoService carritoAnadidoService,
                         ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.repartidorRepository = repartidorRepository;
        this.detallePedidoService = detallePedidoService;
        this.carritoAnadidoService = carritoAnadidoService;
        this.productoRepository = productoRepository;
    }

    public List<PedidoDTO> obtenerTodosPedidos() {
        return pedidoRepository.findAll().stream()
                .map(this::convertirAPedidoDTO)
                .collect(Collectors.toList());
    }

    public Optional<PedidoDTO> obtenerPedidoPorId(Integer id) {
        return pedidoRepository.findById(id)
                .map(this::convertirAPedidoDTO);
    }

    @Transactional
    public PedidoDTO crearPedidoDesdeCarrito(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<CarritoAnadidoDTO> itemsCarrito = carritoAnadidoService.obtenerItemsCarritoPorIdCarrito(usuario.getCarrito().getIdCarrito());
        if (itemsCarrito.isEmpty()) {
            throw new RuntimeException("El carrito del usuario está vacío, no se puede crear un pedido.");
        }

        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDate.now());
        pedido.setHora(LocalTime.now());
        pedido.setUsuario(usuario);
        pedido.setRepartidor(null);

        pedido = pedidoRepository.save(pedido);

        for (CarritoAnadidoDTO item : itemsCarrito) {
            DetallePedidoDTO detalleDTO = new DetallePedidoDTO();
            detalleDTO.setIdPedido(pedido.getIdPedido());
            detalleDTO.setIdProducto(item.getIdProducto());
            detalleDTO.setCantidad(item.getCantidad());

            Producto producto = productoRepository.findById(item.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado durante la creación del detalle del pedido"));
            detalleDTO.setPrecioUnitarioAlMomentoCompra(producto.getPrecioFinal());

            detallePedidoService.crearDetallePedido(detalleDTO);
        }

        carritoAnadidoService.limpiarItemsDeCarrito(usuario.getCarrito().getIdCarrito());

        return convertirAPedidoDTO(pedido);
    }

    @Transactional
    public Optional<PedidoDTO> actualizarPedido(Integer id, PedidoDTO pedidoDTO) {
        return pedidoRepository.findById(id)
                .map(pedidoExistente -> {
                    pedidoExistente.setFecha(pedidoDTO.getFecha());
                    pedidoExistente.setHora(pedidoDTO.getHora());

                    if (pedidoDTO.getIdUsuario() != null) {
                        Usuario usuario = usuarioRepository.findById(pedidoDTO.getIdUsuario())
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                        pedidoExistente.setUsuario(usuario);
                    }

                    if (pedidoDTO.getIdRepartidor() != null) {
                        var repartidor = repartidorRepository.findById(pedidoDTO.getIdRepartidor())
                                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));
                        pedidoExistente.setRepartidor(repartidor);
                    }

                    return convertirAPedidoDTO(pedidoRepository.save(pedidoExistente));
                });
    }

    public boolean eliminarPedido(Integer id) {
        if (pedidoRepository.existsById(id)) {
            pedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private PedidoDTO convertirAPedidoDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFecha(pedido.getFecha());
        dto.setHora(pedido.getHora());
        if (pedido.getUsuario() != null) {
            dto.setIdUsuario(pedido.getUsuario().getIdUsuario());
            dto.setNombreUsuario(pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido());
        }
        if (pedido.getRepartidor() != null) {
            dto.setIdRepartidor(pedido.getRepartidor().getIdRepartidor());
            dto.setNombreRepartidor(pedido.getRepartidor().getNombre() + " " + pedido.getRepartidor().getApellido());
        }
        dto.setDetallesPedido(detallePedidoService.obtenerDetallesPorIdPedido(pedido.getIdPedido()));
        return dto;
    }
}
