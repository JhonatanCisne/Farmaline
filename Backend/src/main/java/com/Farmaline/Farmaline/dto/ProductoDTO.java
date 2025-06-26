package com.farmaline.farmaline.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductoDTO {
    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private Integer stockDisponible;
    private BigDecimal precio;
    private String imagen;
    private LocalDate fechaCaducidad;
    private LocalDate fechaIngreso;
    private BigDecimal igv;
    private BigDecimal precioFinal;

    public ProductoDTO() {
    }

    public ProductoDTO(Integer idProducto, String nombre, String descripcion, Integer stockDisponible, BigDecimal precio, String imagen, LocalDate fechaCaducidad, LocalDate fechaIngreso, BigDecimal igv, BigDecimal precioFinal) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stockDisponible = stockDisponible;
        this.precio = precio;
        this.imagen = imagen;
        this.fechaCaducidad = fechaCaducidad;
        this.fechaIngreso = fechaIngreso;
        this.igv = igv;
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
}