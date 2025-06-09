package com.Farmaline.Farmaline.dto;

public class VehiculoDTO {
    private Integer idVehiculo;
    private String placa;
    private String categoria;
    private String marca;
    private String modelo;
    private Integer anio;

    public VehiculoDTO() {}

    public VehiculoDTO(Integer idVehiculo, String placa, String categoria, String marca, String modelo, Integer anio) {
        this.idVehiculo = idVehiculo;
        this.placa = placa;
        this.categoria = categoria;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
    }

    // Getters y Setters

    public Integer getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Integer idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }
}
