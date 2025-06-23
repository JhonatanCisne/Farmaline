package com.Farmaline.Farmaline.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id; // ¡Importante! Añadir esta importación
import jakarta.persistence.Table; // Para java.time.LocalDate

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

    @Column(name="Stock_Disponible", nullable=false)
    private Integer stockDisponible;

    // --- CAMBIO AQUÍ: float a BigDecimal ---
    @Column(name="Precio", nullable=false, precision=10, scale=2)
    private BigDecimal precio;

    @Column(name="Imagen", nullable=true, length=255)
    private String imagen;

    @Column(name = "Fecha_Caducidad", nullable = true)
    private LocalDate fechaCaducidad;

    @Column(name = "Fecha_Ingreso", nullable = false)
    private LocalDate fechaIngreso;

    // --- CAMBIO AQUÍ: float a BigDecimal ---
    @Column(name = "IGV", nullable = false, precision=5, scale=2)
    private BigDecimal igv;

    // --- CAMBIO AQUÍ: float a BigDecimal ---
    @Column(name = "Precio_Final", nullable = false, precision=10, scale=2)
    private BigDecimal precioFinal;

    public Producto() {
    }

    // --- GETTERS Y SETTERS ACTUALIZADOS PARA BigDecimal ---

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
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

    public Integer getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(Integer stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    // --- GETTER Y SETTER DE PRECIO ACTUALIZADOS ---
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
}