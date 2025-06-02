package com.Farmaline.Farmaline.model;

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
@Table(name="Doble_Verificacion")

public class Doble_Verificacion {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ID_Doble_Verificacion")
    private Integer idDobleVerifiacion;

    @Column(name="Estado_Usuario")
    private String estadoUsuario;

    @Column(name="Estado_Repartidor")
    private String estadoRepartidor;

    @ManyToOne 
    @JoinColumn(name = "ID_Usuario", referencedColumnName = "ID_Usuario")
    private Usuario usuario;

    @ManyToOne 
    @JoinColumn(name = "ID_Repartidor", referencedColumnName = "ID_Repartidor")
    private Repartidor repartidor;

    @OneToOne 
    @JoinColumn(name = "ID_Pedido", referencedColumnName = "ID_Pedido")
    private Pedido pedido;

    public Integer getIdDobleVerifiacion() {
    return idDobleVerifiacion;
    }

    public void setIdDobleVerifiacion(Integer idDobleVerifiacion) {
        this.idDobleVerifiacion = idDobleVerifiacion;
    }

    public String getEstadoUsuario() {
        return estadoUsuario;
    }

    public void setEstadoUsuario(String estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }

    public String getEstadoRepartidor() {
        return estadoRepartidor;
    }

    public void setEstadoRepartidor(String estadoRepartidor) {
        this.estadoRepartidor = estadoRepartidor;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Repartidor getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(Repartidor repartidor) {
        this.repartidor = repartidor;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

}
