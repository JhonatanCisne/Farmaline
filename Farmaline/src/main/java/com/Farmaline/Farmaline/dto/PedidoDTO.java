// src/main/java/com/Farmaline/Farmaline/dto/PedidoDTO.java
package com.Farmaline.Farmaline.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List; // Importar List

public class PedidoDTO {
    private Integer idPedido;
    private LocalDate fecha;
    private LocalTime hora;
    private Integer idUsuario;
    private Integer idCarrito; // Aunque no lo envíes, puede que lo uses internamente en el backend
    private Integer idRepartidor;
    private String estado; // Podrías añadir un estado (Ej: Pendiente, En Camino, Entregado)
    private double total; // <-- Nuevo campo para el total del pedido
    private List<DetallePedidoDTO> detalles; // <-- Nuevo campo para los detalles del pedido

    // Constructor (opcional)
    public PedidoDTO() {}

    public PedidoDTO(Integer idPedido, LocalDate fecha, LocalTime hora, Integer idUsuario, Integer idCarrito, Integer idRepartidor, String estado, double total, List<DetallePedidoDTO> detalles) {
        this.idPedido = idPedido;
        this.fecha = fecha;
        this.hora = hora;
        this.idUsuario = idUsuario;
        this.idCarrito = idCarrito;
        this.idRepartidor = idRepartidor;
        this.estado = estado;
        this.total = total;
        this.detalles = detalles;
    }

    // Getters y Setters (Asegúrate de que los nuevos campos también tengan sus getters y setters)
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

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(Integer idCarrito) {
        this.idCarrito = idCarrito;
    }

    public Integer getIdRepartidor() {
        return idRepartidor;
    }

    public void setIdRepartidor(Integer idRepartidor) {
        this.idRepartidor = idRepartidor;
    }
    
    // Nuevos Getters y Setters
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<DetallePedidoDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedidoDTO> detalles) {
        this.detalles = detalles;
    }
}