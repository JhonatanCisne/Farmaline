package com.farmaline.farmaline.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import jakarta.persistence.CascadeType;
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

    @Column(name="Fecha", nullable = false)
    private LocalDate fecha;

    @Column(name="Hora", nullable = false)
    private LocalTime hora;

    @Column(name="Monto_Total_Pedido", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotalPedido;

    @Column(name="Estado", nullable = false, length = 50)
    private String estado;

    @Column(name="Estado_Usuario_Verificacion", nullable = false, length = 50)
    private String estadoUsuarioVerificacion;

    @Column(name="Estado_Repartidor_Verificacion", nullable = false, length = 50) 
    private String estadoRepartidorVerificacion;

    @ManyToOne 
    @JoinColumn(name = "ID_Usuario", referencedColumnName = "ID_Usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne 
    @JoinColumn(name = "ID_Repartidor", referencedColumnName = "ID_Repartidor", nullable = true)
    private Repartidor repartidor;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public BigDecimal getMontoTotalPedido() {
        return montoTotalPedido;
    }

    public void setMontoTotalPedido(BigDecimal montoTotalPedido) {
        this.montoTotalPedido = montoTotalPedido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadoUsuarioVerificacion() {
        return estadoUsuarioVerificacion;
    }

    public void setEstadoUsuarioVerificacion(String estadoUsuarioVerificacion) {
        this.estadoUsuarioVerificacion = estadoUsuarioVerificacion;
    }

    public String getEstadoRepartidorVerificacion() {
        return estadoRepartidorVerificacion;
    }

    public void setEstadoRepartidorVerificacion(String estadoRepartidorVerificacion) {
        this.estadoRepartidorVerificacion = estadoRepartidorVerificacion;
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