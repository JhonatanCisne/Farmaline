package com.farmaline.farmaline.model;

import java.math.BigDecimal;

import jakarta.persistence.Column; 
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="Producto")
public class Producto {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ID_Producto")
    private Integer idProducto;

    @Column(name="Nombre", nullable=false, length=100)
    private String nombre;

    @Column(name="Descripcion", nullable=true, length=500)
    private String descripcion;

    @Column(name="Precio", nullable=false, precision=10, scale=2)
    private BigDecimal precio;

    @Column(name="Imagen", nullable=true, length=255)
    private String imagen;

    @Column(name = "IGV", nullable = false, precision=5, scale=2)
    private BigDecimal igv;

    @Column(name = "Precio_Final", nullable = false, precision=10, scale=2)
    private BigDecimal precioFinal;

    @Column(name = "Laboratorio", nullable = true, length = 100)
    private String laboratorio;

    public Producto() {
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public BigDecimal getIgv() {
        return igv;
    }

    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(BigDecimal precioFinal) {
        this.precioFinal = precioFinal;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

}