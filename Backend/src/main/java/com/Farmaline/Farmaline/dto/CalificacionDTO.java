package com.farmaline.farmaline.dto;

public class CalificacionDTO {
    private Integer idCalificacion;
    private Integer puntuacion;
    private Integer idUsuario;
    private String nombreUsuario;
    private Integer idProducto;
    private String nombreProducto;

    public CalificacionDTO() {
    }

    public CalificacionDTO(Integer idCalificacion, Integer puntuacion, Integer idUsuario, String nombreUsuario, Integer idProducto, String nombreProducto) {
        this.idCalificacion = idCalificacion;
        this.puntuacion = puntuacion;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
    }

    public Integer getIdCalificacion() {
        return idCalificacion;
    }

    public void setIdCalificacion(Integer idCalificacion) {
        this.idCalificacion = idCalificacion;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
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

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
}