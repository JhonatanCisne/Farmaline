package com.Farmaline.Farmaline.dto;

public class UsuarioRegistroDTO {
    private String nombre;
    private String apellido;
    private String correoElectronico;
    private String domicilio;
    private String telefono;
    private String contrasena; 

    public UsuarioRegistroDTO() {
    }

    public UsuarioRegistroDTO(String nombre, String apellido, String correoElectronico, String domicilio, String telefono, String contrasena) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correoElectronico = correoElectronico;
        this.domicilio = domicilio;
        this.telefono = telefono;
        this.contrasena = contrasena;
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

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}