package com.farmaline.farmaline.dto;

import java.math.BigDecimal;

public class DetallePedidoDTO {
    private Integer idDetallePedido;
    private Integer idPedido;
    private Integer idProducto;
    private int cantidad;
    private BigDecimal precioUnitarioAlMomentoCompra;
    private BigDecimal subtotalDetalle;

    public DetallePedidoDTO() {
    }

    public DetallePedidoDTO(Integer idDetallePedido, Integer idPedido, Integer idProducto, int cantidad, BigDecimal precioUnitarioAlMomentoCompra, BigDecimal subtotalDetalle) {
        this.idDetallePedido = idDetallePedido;
        this.idPedido = idPedido;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitarioAlMomentoCompra = precioUnitarioAlMomentoCompra;
        this.subtotalDetalle = subtotalDetalle;
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

    public BigDecimal getSubtotalDetalle() {
        return subtotalDetalle;
    }

    public void setSubtotalDetalle(BigDecimal subtotalDetalle) {
        this.subtotalDetalle = subtotalDetalle;
    }
}