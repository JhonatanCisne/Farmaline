package com.farmaline.farmaline.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="Repartidor")

public class Repartidor {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ID_Repartidor")
    private Integer idRepartidor;

    @Column(name="Nombre")
    private String nombre;

    @Column(name="Apellido")
    private String apellido;

    @Column(name="Telefono")
    private String telefono;

    @Column(name="Contrasena")
    private String contrasena;

    @OneToOne   
    @JoinColumn(name = "ID_Vehiculo", referencedColumnName = "ID_Vehiculo")
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "ID_Administrador", referencedColumnName = "ID_Administrador") 
    private Administrador administrador;

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

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Administrador getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Administrador administrador) {
        this.administrador = administrador;
    }
}