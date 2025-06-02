package com.Farmaline.Farmaline.dto;

public class UsuarioLoginDTO {
    private String correoElectronico;
    private String contrasena;

    public UsuarioLoginDTO() {}

    public UsuarioLoginDTO(String correoElectronico, String contrasena) {
        this.correoElectronico = correoElectronico;
        this.contrasena = contrasena;
    }


    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
