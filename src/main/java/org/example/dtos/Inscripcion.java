package org.example.dtos;

import java.util.Date;

public class Inscripcion {
    private String id;
    private String usuarioId;
    private String competenciaId;
    private Date fechaInscripcion;
    private String estado; // PENDIENTE, CONFIRMADA, CANCELADA

    public Inscripcion(String id, String usuarioId, String competenciaId) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.competenciaId = competenciaId;
        this.fechaInscripcion = new Date();
        this.estado = "PENDIENTE";
    }

    public void confirmarInscripcion() {
        this.estado = "CONFIRMADA";
    }

    public void cancelarInscripcion() {
        this.estado = "CANCELADA";
    }

    public String getCompetenciaId() { //metodo para obtener compID
        return competenciaId;
    }

    public String getEstado() { //metodo para obtener el estado
        return estado;
    }

    public String getUsuarioId() {
        return usuarioId;
    }
}
