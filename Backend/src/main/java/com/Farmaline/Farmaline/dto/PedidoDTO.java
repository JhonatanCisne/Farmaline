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
    private String estado;
    private String estadoUsuarioVerificacion;
    private String estadoRepartidorVerificacion;
    private Integer idUsuario;
    private Integer idRepartidor;
    private String nombreRepartidor;

    private String nombreUsuario;
    private String apellidoUsuario;
    private String correoUsuario;
    private String domicilioUsuario;
    private String telefonoUsuario;

    private List<DetallePedidoDTO> detallesPedido; 

    public PedidoDTO() {
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public String getNombreRepartidor() {
        return nombreRepartidor;
    }

    public void setNombreRepartidor(String nombreRepartidor) {
        this.nombreRepartidor = nombreRepartidor;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getApellidoUsuario() {
        return apellidoUsuario;
    }

    public void setApellidoUsuario(String apellidoUsuario) {
        this.apellidoUsuario = apellidoUsuario;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public String getDomicilioUsuario() {
        return domicilioUsuario;
    }

    public void setDomicilioUsuario(String domicilioUsuario) {
        this.domicilioUsuario = domicilioUsuario;
    }

    public String getTelefonoUsuario() {
        return telefonoUsuario;
    }

    public void setTelefonoUsuario(String telefonoUsuario) {
        this.telefonoUsuario = telefonoUsuario;
    }

    public List<DetallePedidoDTO> getDetallesPedido() {
        return detallesPedido;
    }

    public void setDetallesPedido(List<DetallePedidoDTO> detallesPedido) {
        this.detallesPedido = detallesPedido;
    }
}