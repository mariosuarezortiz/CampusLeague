package org.example.dtos;

import java.util.Date;

public class Competencia {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Date fechaInicio;
    private Date fechaFin;
    private String estado; // ACTIVA, FINALIZADA, CANCELADA
    private String institucionId;
    private double costoInscripcion;
    private Integer maxParticipantes;
    private Integer participantes;


    public Competencia(Integer id, String nombre, String descripcion, Date fechaInicio, Date fechaFin, String institucionId, double costoInscripcion, Integer maxParticipantes,
                       String estado,Integer participantes) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.institucionId = institucionId;
        this.costoInscripcion = costoInscripcion;
        this.maxParticipantes = maxParticipantes;
        this.participantes = participantes;
    }

    public void cancelarCompetencia() {
        this.estado = "CANCELADA";
    }

    public void finalizarCompetencia() {
        this.estado = "FINALIZADA";
    }
    public void empezarCompetencia() {
        this.estado = "COMENZADO";
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public String getInstitucionId() {
        return institucionId;
    }

    public double getCostoInscripcion() {
        return costoInscripcion;
    }

    public Integer getMaxParticipantes() {
        return maxParticipantes;
    }

    public Integer getParticipantes(){
        return participantes;
    }

    public void setParticipantes(int participantes){
        this.participantes = participantes;
    }

    public void setEstado(String participantes){
        this.estado = participantes;
    }
}