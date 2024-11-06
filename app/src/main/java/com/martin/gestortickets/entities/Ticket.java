package com.martin.gestortickets.entities;

import java.util.Objects;

public class Ticket {
    private Integer id;
    private String titulo;
    private String descripcion;
    private Usuario creador;
    private Usuario tecnico;
    private Estado estado;

    public Ticket() {}

    public Ticket(Integer id, String titulo, String descripcion, Usuario creador, Usuario tecnico, Estado estado) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.creador = creador;
        this.tecnico = tecnico;
        this.estado = estado;
    }

    public Ticket(String titulo, String descripcion, Usuario creador, Usuario tecnico, Estado estado) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.creador = creador;
        this.tecnico = tecnico;
        this.estado = estado;
    }

    public Ticket(Integer id, String titulo, String descripcion, Usuario creador) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.creador = creador;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Usuario getCreador() {
        return creador;
    }

    public void setCreador(Usuario creador) {
        this.creador = creador;
    }

    public Usuario getTecnico() {
        return tecnico;
    }

    public void setTecnico(Usuario tecnico) {
        this.tecnico = tecnico;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(getId(), ticket.getId()) && Objects.equals(getTitulo(), ticket.getTitulo()) && Objects.equals(getDescripcion(), ticket.getDescripcion()) && Objects.equals(getCreador(), ticket.getCreador()) && Objects.equals(getTecnico(), ticket.getTecnico()) && Objects.equals(getEstado(), ticket.getEstado());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitulo(), getDescripcion(), getCreador(), getTecnico(), getEstado());
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", creador=" + creador +
                ", tecnico=" + tecnico +
                ", estado=" + estado +
                '}';
    }
}
