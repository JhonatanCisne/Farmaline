package com.farmaline.farmaline.dto;

public class DobleVerificacionDTO {
    private Integer idDobleVerificacion;
    private String estadoUsuario;
    private String estadoRepartidor;
    private Integer idUsuario;
    private String nombreUsuario;
    private Integer idRepartidor;
    private String nombreRepartidor;
    private Integer idPedido;

    public DobleVerificacionDTO() {
    }

    public DobleVerificacionDTO(Integer idDobleVerificacion, String estadoUsuario, String estadoRepartidor, Integer idUsuario, String nombreUsuario, Integer idRepartidor, String nombreRepartidor, Integer idPedido) {
        this.idDobleVerificacion = idDobleVerificacion;
        this.estadoUsuario = estadoUsuario;
        this.estadoRepartidor = estadoRepartidor;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.idRepartidor = idRepartidor;
        this.nombreRepartidor = nombreRepartidor;
        this.idPedido = idPedido;
    }

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

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }
}