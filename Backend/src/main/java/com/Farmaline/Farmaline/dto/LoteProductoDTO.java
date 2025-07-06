package com.farmaline.farmaline.dto;

import java.time.LocalDate;

public class LoteProductoDTO {
    private Integer idLoteProducto;
    private Integer idProducto; 
    private String numeroLote;
    private LocalDate fechaCaducidad;
    private int cantidadDisponible;
    private LocalDate fechaIngreso;

    public LoteProductoDTO() {
    }

    public Integer getIdLoteProducto() {
        return idLoteProducto;
    }

    public void setIdLoteProducto(Integer idLoteProducto) {
        this.idLoteProducto = idLoteProducto;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
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
}