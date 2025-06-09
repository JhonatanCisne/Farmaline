package com.Farmaline.Farmaline.dto;

public class Carrito_AnadidoDTO {
    private Integer idCarritoAnadido;
    private int cantidad;
    private Integer idProducto; 
    private Integer idCarrito;  

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

    public Integer getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(Integer idCarrito) {
        this.idCarrito = idCarrito;
    }
}
