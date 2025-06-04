package com.Farmaline.Farmaline.dto;

public class DetallePedidoDTO {
    private Integer idDetallePedido; 
    private Integer idPedido;
    private Integer idProducto;
    private String nombreProducto; 
    private String imagenProducto; 
    private double precioUnitario;
    private Integer cantidad;
    private double subtotalLinea;

    public DetallePedidoDTO() {}

    public DetallePedidoDTO(Integer idDetallePedido, Integer idPedido, Integer idProducto, String nombreProducto, String imagenProducto, double precioUnitario, Integer cantidad, double subtotalLinea) {
        this.idDetallePedido = idDetallePedido;
        this.idPedido = idPedido;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.imagenProducto = imagenProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.subtotalLinea = subtotalLinea;
    }

    public Integer getIdDetallePedido() {
        return idDetallePedido;
    }

    public void setIdDetallePedido(Integer idDetallePedido) {
        this.idDetallePedido = idDetallePedido;
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
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

    public String getImagenProducto() {
        return imagenProducto;
    }

    public void setImagenProducto(String imagenProducto) {
        this.imagenProducto = imagenProducto;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotalLinea() {
        return subtotalLinea;
    }

    public void setSubtotalLinea(double subtotalLinea) {
        this.subtotalLinea = subtotalLinea;
    }
}