package com.martin.gestortickets.entities;

import java.util.Objects;

public class Notificacion {
    private int id;
    private String mensaje;
    private Boolean visto;
    private Usuario remitente;

    public Notificacion(int id, String mensaje, boolean visto, Usuario remitente) {
        this.id = id;
        this.mensaje = mensaje;
        this.visto = visto;
        this.remitente = remitente;
    }

    public Notificacion(String mensaje, Usuario remitente) {
        this.mensaje = mensaje;
        this.remitente = remitente;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Boolean getVisto() {
        return visto;
    }

    public void setVisto(Boolean visto) {
        this.visto = visto;
    }

    public Usuario getRemitente() {
        return remitente;
    }

    public void setRemitente(Usuario remitente) {
        this.remitente = remitente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notificacion that = (Notificacion) o;
        return getId() == that.getId() && Objects.equals(getMensaje(), that.getMensaje()) && Objects.equals(getVisto(), that.getVisto()) && Objects.equals(getRemitente(), that.getRemitente());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getMensaje(), getVisto(), getRemitente());
    }

    @Override
    public String toString() {
        return "Notificacion{" +
                "id=" + id +
                ", mensaje='" + mensaje + '\'' +
                ", visto=" + visto +
                ", remitente=" + remitente +
                '}';
    }
}
