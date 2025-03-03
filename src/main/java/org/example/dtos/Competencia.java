package org.example.dtos;

import java.util.Date;

public class Competencia {
    private String id;
    private String nombre;
    private String descripcion;
    private Date fechaInicio;
    private Date fechaFin;
    private String estado; // ACTIVA, FINALIZADA, CANCELADA
    private String institucionId;
    private double costoInscripcion;

    public Competencia(String id, String nombre, String descripcion, Date fechaInicio, Date fechaFin, String institucionId, double costoInscripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = "ACTIVA";
        this.institucionId = institucionId;
        this.costoInscripcion = costoInscripcion;
    }

    public void cancelarCompetencia() {
        this.estado = "CANCELADA";
    }

    public void finalizarCompetencia() {
        this.estado = "FINALIZADA";
    }

    public double getCostoInscripcion() {
        return costoInscripcion;
    }
}