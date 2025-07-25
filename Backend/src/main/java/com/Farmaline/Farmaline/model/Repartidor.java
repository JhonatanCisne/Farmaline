package com.farmaline.farmaline.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="Repartidor")
public class Repartidor {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ID_Repartidor")
    private Integer idRepartidor;

    @Column(name="Nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name="Apellido", nullable = false, length = 50)
    private String apellido;

    @Column(name="Correo_Electronico", nullable = false, unique = true, length = 100)
    private String correoElectronico;

    @Column(name="Telefono", nullable = false, unique = true, length = 20)
    private String telefono;

    @Column(name="Placa", nullable = false, unique = true, length = 10)
    private String placa;

    @Column(name="Contrasena", nullable = false, length = 100) 
    private String contrasena;

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

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
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

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
}