package com.Farmaline.Farmaline.dto;

public class Doble_VerificacionDTO {
    private Integer idDobleVerificacion;
    private String estadoUsuario;
    private String estadoRepartidor;
    private Integer idUsuario;
    private Integer idRepartidor;
    private Integer idPedido;

    public Integer getIdDobleVerificacion() {
        return idDobleVerificacion;
    }

    public void setIdDobleVerificacion(Integer idDobleVerificacion) {
        this.idDobleVerificacion = idDobleVerificacion;
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
