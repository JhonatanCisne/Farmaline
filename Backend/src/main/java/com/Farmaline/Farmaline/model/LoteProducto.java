package com.farmaline.farmaline.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class LoteProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLoteProducto;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(nullable = false, unique = true) 
    private String numeroLote;

    @Column(nullable = false)
    private LocalDate fechaCaducidad;

    @Column(nullable = false)
    private int cantidadDisponible; 

    @Column(nullable = false)
    private LocalDate fechaIngreso; 

    public LoteProducto() {
    }

    public LoteProducto(Producto producto, String numeroLote, LocalDate fechaCaducidad, int cantidadDisponible, LocalDate fechaIngreso) {
        this.producto = producto;
        this.numeroLote = numeroLote;
        this.fechaCaducidad = fechaCaducidad;
        this.cantidadDisponible = cantidadDisponible;
        this.fechaIngreso = fechaIngreso;
    }

    public Integer getIdLoteProducto() {
        return idLoteProducto;
    }

    public void setIdLoteProducto(Integer idLoteProducto) {
        this.idLoteProducto = idLoteProducto;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    @Override
    public String toString() {
        return "LoteProducto{" +
               "idLoteProducto=" + idLoteProducto +
               ", producto=" + (producto != null ? producto.getNombre() : "null") +
               ", numeroLote='" + numeroLote + '\'' +
               ", fechaCaducidad=" + fechaCaducidad +
               ", cantidadDisponible=" + cantidadDisponible +
               ", fechaIngreso=" + fechaIngreso +
               '}';
    }
}