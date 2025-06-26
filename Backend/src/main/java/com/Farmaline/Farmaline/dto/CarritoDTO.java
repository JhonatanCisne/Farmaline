package com.farmaline.farmaline.dto;

import java.util.List;

public class CarritoDTO {
    private Integer idCarrito;
    private Integer idUsuario;
    private String nombreUsuario;
    private List<CarritoAnadidoDTO> itemsCarrito;

    public CarritoDTO() {
    }

    public CarritoDTO(Integer idCarrito, Integer idUsuario, String nombreUsuario, List<CarritoAnadidoDTO> itemsCarrito) {
        this.idCarrito = idCarrito;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.itemsCarrito = itemsCarrito;
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

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public List<CarritoAnadidoDTO> getItemsCarrito() {
        return itemsCarrito;
    }

    public void setItemsCarrito(List<CarritoAnadidoDTO> itemsCarrito) {
        this.itemsCarrito = itemsCarrito;
    }
}