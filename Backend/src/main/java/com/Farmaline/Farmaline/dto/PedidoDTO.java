package com.farmaline.farmaline.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PedidoDTO {
    private Integer idPedido;
    private LocalDate fecha;
    private LocalTime hora;
    private Integer idUsuario;
    private String nombreUsuario;
    private Integer idRepartidor;
    private String nombreRepartidor;
    private List<DetallePedidoDTO> detallesPedido;

    public PedidoDTO() {
    }

    public PedidoDTO(Integer idPedido, LocalDate fecha, LocalTime hora, Integer idUsuario, String nombreUsuario, Integer idRepartidor, String nombreRepartidor, List<DetallePedidoDTO> detallesPedido) {
        this.idPedido = idPedido;
        this.fecha = fecha;
        this.hora = hora;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.idRepartidor = idRepartidor;
        this.nombreRepartidor = nombreRepartidor;
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

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public Integer getIdRepartidor() {
        return idRepartidor;
    }

    public void setIdRepartidor(Integer idRepartidor) {
        this.idRepartidor = idRepartidor;
    }

    public String getNombreRepartidor() {
        return nombreRepartidor;
    }

    public void setNombreRepartidor(String nombreRepartidor) {
        this.nombreRepartidor = nombreRepartidor;
    }

    public List<DetallePedidoDTO> getDetallesPedido() {
        return detallesPedido;
    }

    public void setDetallesPedido(List<DetallePedidoDTO> detallesPedido) {
        this.detallesPedido = detallesPedido;
    }
}