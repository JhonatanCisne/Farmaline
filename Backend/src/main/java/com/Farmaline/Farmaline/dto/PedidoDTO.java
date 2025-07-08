package com.farmaline.farmaline.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PedidoDTO {
    private Integer idPedido;
    private LocalDate fecha;
    private LocalTime hora;
    private BigDecimal montoTotalPedido;
    private Integer idUsuario;
    private Integer idRepartidor;
    private String estadoPedido; 
    private String estadoUsuarioVerificacion; 
    private String estadoRepartidorVerificacion;
    private String nombreUsuario;
    private String domicilioUsuario;
    private List<DetallePedidoDTO> detallesPedido;

    public PedidoDTO() {
    }

    public PedidoDTO(Integer idPedido, LocalDate fecha, LocalTime hora, BigDecimal montoTotalPedido, Integer idUsuario, Integer idRepartidor, String estadoPedido, String estadoUsuarioVerificacion, String estadoRepartidorVerificacion, List<DetallePedidoDTO> detallesPedido) {
        this.idPedido = idPedido;
        this.fecha = fecha;
        this.hora = hora;
        this.montoTotalPedido = montoTotalPedido;
        this.idUsuario = idUsuario;
        this.idRepartidor = idRepartidor;
        this.estadoPedido = estadoPedido;
        this.estadoUsuarioVerificacion = estadoUsuarioVerificacion;
        this.estadoRepartidorVerificacion = estadoRepartidorVerificacion;
        this.detallesPedido = detallesPedido;
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public BigDecimal getMontoTotalPedido() {
        return montoTotalPedido;
    }

    public void setMontoTotalPedido(BigDecimal montoTotalPedido) {
        this.montoTotalPedido = montoTotalPedido;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdRepartidor() {
        return idRepartidor;
    }

    public void setIdRepartidor(Integer idRepartidor) {
        this.idRepartidor = idRepartidor;
    }

    public String getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(String estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public String getEstadoUsuarioVerificacion() {
        return estadoUsuarioVerificacion;
    }

    public void setEstadoUsuarioVerificacion(String estadoUsuarioVerificacion) {
        this.estadoUsuarioVerificacion = estadoUsuarioVerificacion;
    }

    public String getEstadoRepartidorVerificacion() {
        return estadoRepartidorVerificacion;
    }

    public void setEstadoRepartidorVerificacion(String estadoRepartidorVerificacion) {
        this.estadoRepartidorVerificacion = estadoRepartidorVerificacion;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getDomicilioUsuario() {
        return domicilioUsuario;
    }

    public void setDomicilioUsuario(String domicilioUsuario) {
        this.domicilioUsuario = domicilioUsuario;
    }

    public List<DetallePedidoDTO> getDetallesPedido() {
        return detallesPedido;
    }

    public void setDetallesPedido(List<DetallePedidoDTO> detallesPedido) {
        this.detallesPedido = detallesPedido;
    }
}