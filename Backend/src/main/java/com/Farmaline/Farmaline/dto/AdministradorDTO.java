package com.farmaline.farmaline.dto;

public class AdministradorDTO {
    private Integer idAdministrador;
    private String nombre;
    private String apellido;
    private String contrasena;

    public AdministradorDTO() {
    }

    public AdministradorDTO(Integer idAdministrador, String nombre, String apellido, String contrasena) {
        this.idAdministrador = idAdministrador;
        this.nombre = nombre;
        this.apellido = apellido;
        this.contrasena = contrasena;
    }

    public Integer getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(Integer idAdministrador) {
        this.idAdministrador = idAdministrador;
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

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}