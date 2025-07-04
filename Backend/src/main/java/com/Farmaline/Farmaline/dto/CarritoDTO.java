package com.farmaline.farmaline.dto;

public class CarritoDTO {
    private Integer idCarrito;
    private Integer idUsuario;

    public CarritoDTO() {
    }

    public CarritoDTO(Integer idCarrito, Integer idUsuario) {
        this.idCarrito = idCarrito;
        this.idUsuario = idUsuario;
    }

    public Integer getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(Integer idCarrito) {
        this.idCarrito = idCarrito;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
}