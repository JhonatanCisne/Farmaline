package com.farmaline.farmaline.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "Detalle_Pedido")
public class DetallePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Detalle_Pedido")
    private Integer idDetallePedido;

    @ManyToOne
    @JoinColumn(name = "ID_Pedido", referencedColumnName = "ID_Pedido")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "ID_Producto", referencedColumnName = "ID_Producto")
    private Producto producto;

    @Column(name = "Cantidad")
    private int cantidad;

    @Column(name = "Precio_Unitario_Al_Momento_Compra", precision = 10, scale = 2)
    private BigDecimal precioUnitarioAlMomentoCompra;

    public Integer getIdDetallePedido() {
        return idDetallePedido;
    }

    public void setIdDetallePedido(Integer idDetallePedido) {
        this.idDetallePedido = idDetallePedido;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
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