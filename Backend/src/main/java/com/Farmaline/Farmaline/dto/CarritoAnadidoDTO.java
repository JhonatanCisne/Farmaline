package com.farmaline.farmaline.dto;

public class CarritoAnadidoDTO {
    private Integer idCarritoAnadido;
    private int cantidad;
    private Integer idProducto;
    private String nombreProducto;
    private Integer idCarrito;

    public CarritoAnadidoDTO() {
    }

    public CarritoAnadidoDTO(Integer idCarritoAnadido, int cantidad, Integer idProducto, String nombreProducto, Integer idCarrito) {
        this.idCarritoAnadido = idCarritoAnadido;
        this.cantidad = cantidad;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.idCarrito = idCarrito;
    }

    public Integer getIdCarritoAnadido() {
        return idCarritoAnadido;
    }

    public void setIdCarritoAnadido(Integer idCarritoAnadido) {
        this.idCarritoAnadido = idCarritoAnadido;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
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

    public Integer getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(Integer idCarrito) {
        this.idCarrito = idCarrito;
    }
}