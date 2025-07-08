package com.farmaline.farmaline.dto;

public class RegistroDTO {
    private Integer idRegistro;
    private Integer idPedido;

    public RegistroDTO() {
    }

    public RegistroDTO(Integer idRegistro, Integer idPedido) {
        this.idRegistro = idRegistro;
        this.idPedido = idPedido;
    }

    public Integer getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(Integer idRegistro) {
        this.idRegistro = idRegistro;
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }
}