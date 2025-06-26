package com.farmaline.farmaline.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="Pedido")

public class Pedido {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ID_Pedido")
    private Integer idPedido;

    @Column(name="Fecha")
    private LocalDate fecha;

    @Column(name="Hora")
    private LocalTime hora;

    @ManyToOne 
    @JoinColumn(name = "ID_Usuario", referencedColumnName = "ID_Usuario")
    private Usuario usuario;

    @ManyToOne 
    @JoinColumn(name = "ID_Repartidor", referencedColumnName = "ID_Repartidor")
    private Repartidor repartidor;

    @OneToMany(mappedBy = "pedido")
    private Set<DetallePedido> detallesPedido;

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
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

    public Set<DetallePedido> getDetallesPedido() {
        return detallesPedido;
    }

    public void setDetallesPedido(Set<DetallePedido> detallesPedido) {
        this.detallesPedido = detallesPedido;
    }
}