package com.farmaline.farmaline.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="Carrito_Anadido")

public class Carrito_Anadido {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ID_Carrito_Anadido")
    private Integer idCarritoAnadido;

    @Column(name="Cantidad")
    private int cantidad;

    @ManyToOne
    @JoinColumn(name = "ID_Producto", referencedColumnName = "ID_Producto")
    private Producto producto;

    @ManyToOne 
    @JoinColumn(name = "ID_Carrito", referencedColumnName = "ID_Carrito")
    private Carrito carrito;
    
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

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }
}