package org.example.dtos;

import java.util.Date;

public class Contenido {
    private String id;
    private String tipo;
    private String descripcion;
    private Date fechaPublicacion;
    private String autorId;

    public Contenido(String id, String tipo, String descripcion, String autorId) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fechaPublicacion = new Date();
        this.autorId = autorId;
    }

    public void actualizarDescripcion(String nuevaDescripcion) {
        this.descripcion = nuevaDescripcion;
    }
}