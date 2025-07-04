package com.farmaline.farmaline.dto;

public class DobleVerificacionDTO {
    private Integer idDobleVerifiacion;
    private String estadoUsuario;
    private String estadoRepartidor;
    private Integer idUsuario;   // ID del usuario involucrado en la verificación
    private Integer idRepartidor; // ID del repartidor involucrado en la verificación
    private Integer idPedido;    // ID del pedido que se está verificando

    public DobleVerificacionDTO() {
    }

    public DobleVerificacionDTO(Integer idDobleVerifiacion, String estadoUsuario, String estadoRepartidor, Integer idUsuario, Integer idRepartidor, Integer idPedido) {
        this.idDobleVerifiacion = idDobleVerifiacion;
        this.estadoUsuario = estadoUsuario;
        this.estadoRepartidor = estadoRepartidor;
        this.idUsuario = idUsuario;
        this.idRepartidor = idRepartidor;
        this.idPedido = idPedido;
    }

    public Integer getIdDobleVerifiacion() {
        return idDobleVerifiacion;
    }

    public void setIdDobleVerifiacion(Integer idDobleVerifiacion) {
        this.idDobleVerifiacion = idDobleVerifiacion;
    }

    public String getEstadoUsuario() {
        return estadoUsuario;
    }

    public void setEstadoUsuario(String estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }

    public String getEstadoRepartidor() {
        return estadoRepartidor;
    }

    public void setEstadoRepartidor(String estadoRepartidor) {
        this.estadoRepartidor = estadoRepartidor;
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

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }
}