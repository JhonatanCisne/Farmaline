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
    private List<DetallePedidoDTO> detallesPedido; 

    public PedidoDTO() {
    }

    public PedidoDTO(Integer idPedido, LocalDate fecha, LocalTime hora, BigDecimal montoTotalPedido, Integer idUsuario, Integer idRepartidor, List<DetallePedidoDTO> detallesPedido) {
        this.idPedido = idPedido;
        this.fecha = fecha;
        this.hora = hora;
        this.montoTotalPedido = montoTotalPedido;
        this.idUsuario = idUsuario;
        this.idRepartidor = idRepartidor;
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

    public List<DetallePedidoDTO> getDetallesPedido() {
        return detallesPedido;
    }

    public void setDetallesPedido(List<DetallePedidoDTO> detallesPedido) {
        this.detallesPedido = detallesPedido;
    }
}