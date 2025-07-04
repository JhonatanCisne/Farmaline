package com.farmaline.farmaline.dto;

public class CalificacionDTO {
    private Integer idCalificacion;
    private Integer puntuacion;
    private Integer idUsuario; 
    private Integer idProducto; 

    public CalificacionDTO() {
    }

    public CalificacionDTO(Integer idCalificacion, Integer puntuacion, Integer idUsuario, Integer idProducto) {
        this.idCalificacion = idCalificacion;
        this.puntuacion = puntuacion;
        this.idUsuario = idUsuario;
        this.idProducto = idProducto;
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

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }
}