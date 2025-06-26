package com.farmaline.farmaline.dto;

import java.math.BigDecimal;

public class DetallePedidoDTO {
    private Integer idDetallePedido;
    private Integer idPedido;
    private Integer idProducto;
    private String nombreProducto;
    private int cantidad;
    private BigDecimal precioUnitarioAlMomentoCompra;

    public DetallePedidoDTO() {
    }

    public DetallePedidoDTO(Integer idDetallePedido, Integer idPedido, Integer idProducto, String nombreProducto, int cantidad, BigDecimal precioUnitarioAlMomentoCompra) {
        this.idDetallePedido = idDetallePedido;
        this.idPedido = idPedido;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitarioAlMomentoCompra = precioUnitarioAlMomentoCompra;
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

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitarioAlMomentoCompra() {
        return precioUnitarioAlMomentoCompra;
    }

    public void setPrecioUnitarioAlMomentoCompra(BigDecimal precioUnitarioAlMomentoCompra) {
        this.precioUnitarioAlMomentoCompra = precioUnitarioAlMomentoCompra;
    }
}