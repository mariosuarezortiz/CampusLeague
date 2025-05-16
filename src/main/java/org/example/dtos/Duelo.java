package org.example.dtos;

public class Duelo {
    private String usuario1Id;
    private String usuario2Id;
    private String competenciaId;
    private String ganadorId;

    public Duelo(String usuario1Id, String usuario2Id, String competenciaId) {
        this.usuario1Id = usuario1Id;
        this.usuario2Id = usuario2Id;
        this.competenciaId = competenciaId;
        this.ganadorId = "0"; // Inicialmente no hay ganador
    }

    public String getUsuario1Id() {
        return usuario1Id;
    }

    public String getUsuario2Id() {
        return usuario2Id;
    }

    public String getCompetenciaId() {
        return competenciaId;
    }

    public String getGanadorId() {
        return ganadorId;
    }

    public void setGanadorId(String ganadorId) {
        this.ganadorId = ganadorId;
    }

    @Override
    public String toString() {
        return String.format("Duelo [Competencia: %s, Usuario1: %s, Usuario2: %s, Ganador: %s]",
                competenciaId, usuario1Id, usuario2Id, ganadorId);
    }

    // Método para obtener el estado del duelo
    public String getEstado() {
        return ganadorId.equals("0") ? "PENDIENTE" : "FINALIZADO";
    }
}
