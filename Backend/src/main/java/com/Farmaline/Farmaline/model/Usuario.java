package com.farmaline.farmaline.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="Usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ID_Usuario")
    private Integer idUsuario;

    @Column(name="Nombre", nullable=false, length=50)
    private String nombre;

    @Column(name="Apellido", nullable=false, length=50)
    private String apellido;

    @Column(name="Correo_Electronico", nullable=false, unique = true, length=100)
    private String correoElectronico;

    @Column(name="Domicilio", nullable=false, length=255)
    private String domicilio;

    @Column(name="Telefono", nullable=false, unique = true, length=20)
    private String telefono;
    
    @Column(name="Contrasena", nullable=false, length=100) // Se recomienda almacenar contraseñas hasheadas
    private String contrasena;

    @OneToOne(mappedBy = "usuario")
    private Carrito carrito;

    public Integer getIdUsuario(){
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario){
        this.idUsuario=idUsuario;
    }

    public String getNombre(){
        return nombre;
    }

    public void setNombre(String nombre){
        this.nombre=nombre;
    }

    public String getApellido(){
        return apellido;
    }

    public void setApellido(String apellido){
        this.apellido=apellido;
    }
    
    public String getCorreoElectronico(){
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico){
        this.correoElectronico=correoElectronico;
    }

    public String getDomicilio(){
        return domicilio;
    }

    public void setDomicilio(String domicilio){
        this.domicilio=domicilio;
    }

    public String getTelefono(){
        return telefono;
    }

    public void setTelefono(String telefono){
        this.telefono=telefono;
    }

    public String getContrasena(){
        return contrasena;
    }

    public void setContrasena(String contrasena){
        this.contrasena=contrasena;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }
}