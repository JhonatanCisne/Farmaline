package com.Farmaline.Farmaline.dto;

public class AdministradorDTO {
    private String nombre;
    private String apellido;

    public AdministradorDTO() {}

    public AdministradorDTO(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
}
