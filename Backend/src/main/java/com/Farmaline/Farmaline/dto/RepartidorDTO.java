package com.farmaline.farmaline.dto;

public class RepartidorDTO {
    private Integer idRepartidor;
    private String nombre;
    private String apellido;
    private String correo_Electronico;
    private String telefono;
    private String placa;
    private String contrasena; 

    public RepartidorDTO() {
    }

    public RepartidorDTO(Integer idRepartidor, String nombre, String apellido, String correo_Electronico, String telefono, String placa, String contrasena) {
        this.idRepartidor = idRepartidor;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo_Electronico = correo_Electronico;
        this.telefono = telefono;
        this.placa = placa;
        this.contrasena = contrasena;
    }

    public Integer getIdRepartidor() {
        return idRepartidor;
    }

    public void setIdRepartidor(Integer idRepartidor) {
        this.idRepartidor = idRepartidor;
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

    public String getCorreo_Electronico() {
        return correo_Electronico;
    }

    public void setCorreo_Electronico(String correo_Electronico) {
        this.correo_Electronico = correo_Electronico;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}