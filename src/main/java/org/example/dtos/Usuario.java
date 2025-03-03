package org.example.dtos;

public class Usuario {
    private String id;
    private String nombre;
    private String correo;
    private String contraseña;
    private String rol; // ADMINISTRADOR, ESTUDIANTE, INSTITUCIÓN

    public Usuario(String id, String nombre, String correo, String contraseña, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.contraseña = contraseña;

    }

    public void cambiarRol(String nuevoRol) {
        this.rol = nuevoRol;
    }

    public void actualizarCorreo(String nuevoCorreo) {
        this.correo = nuevoCorreo;
    }

    public String getNombre() {
        return nombre;
    }
}