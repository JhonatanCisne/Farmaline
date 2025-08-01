package com.farmaline.farmaline.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint; 

@Entity
@Table(name="Calificacion", uniqueConstraints = { 
    @UniqueConstraint(columnNames = {"ID_Usuario", "ID_Producto"})
})
public class Calificacion {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ID_Calificacion")
    private Integer idCalificacion;

    @Column(name="Puntuacion", nullable = false) 
    private Integer puntuacion;

    @ManyToOne
    @JoinColumn(name = "ID_Usuario", referencedColumnName = "ID_Usuario", nullable = false) 
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "ID_Producto", referencedColumnName = "ID_Producto", nullable = false) 
    private Producto producto;

    public Integer getIdCalificacion() {
        return idCalificacion;
    }

    public void setIdCalificacion(Integer idCalificacion) {
        this.idCalificacion = idCalificacion;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
}