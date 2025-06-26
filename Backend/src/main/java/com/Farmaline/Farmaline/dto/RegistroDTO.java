package com.farmaline.farmaline.dto;

public class RegistroDTO {
    private Integer idRegistro;
    private Integer idPedido;
    private Integer idDobleVerificacion;

    public RegistroDTO() {
    }

    public RegistroDTO(Integer idRegistro, Integer idPedido, Integer idDobleVerificacion) {
        this.idRegistro = idRegistro;
        this.idPedido = idPedido;
        this.idDobleVerificacion = idDobleVerificacion;
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

    public Integer getIdDobleVerificacion() {
        return idDobleVerificacion;
    }

    public void setIdDobleVerificacion(Integer idDobleVerificacion) {
        this.idDobleVerificacion = idDobleVerificacion;
    }
}