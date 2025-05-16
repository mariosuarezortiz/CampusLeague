package org.example.dtos;

import java.util.Date;

public class Inscripcion {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getCompetenciaId() {
        return competenciaId;
    }

    public void setCompetenciaId(String competenciaId) {
        this.competenciaId = competenciaId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    private String id;
    private String usuarioId;
    private String competenciaId;

    private String estado;

    public Inscripcion(String id, String usuarioId, String competenciaId, String estado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.competenciaId = competenciaId;
        this.estado = estado;
    }

}