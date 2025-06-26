package com.farmaline.farmaline.dto;

public class RepartidorDTO {
    private Integer idRepartidor;
    private String nombre;
    private String apellido;
    private String telefono;
    private String contrasena;
    private Integer idVehiculo;
    private String placaVehiculo;
    private Integer idAdministrador;
    private String nombreAdministrador;

    public RepartidorDTO() {
    }

    public RepartidorDTO(Integer idRepartidor, String nombre, String apellido, String telefono, String contrasena, Integer idVehiculo, String placaVehiculo, Integer idAdministrador, String nombreAdministrador) {
        this.idRepartidor = idRepartidor;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.contrasena = contrasena;
        this.idVehiculo = idVehiculo;
        this.placaVehiculo = placaVehiculo;
        this.idAdministrador = idAdministrador;
        this.nombreAdministrador = nombreAdministrador;
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

    public Integer getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Integer idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getPlacaVehiculo() {
        return placaVehiculo;
    }

    public void setPlacaVehiculo(String placaVehiculo) {
        this.placaVehiculo = placaVehiculo;
    }

    public Integer getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(Integer idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public String getNombreAdministrador() {
        return nombreAdministrador;
    }

    public void setNombreAdministrador(String nombreAdministrador) {
        this.nombreAdministrador = nombreAdministrador;
    }
}