package com.martin.gestortickets.entities;

import java.util.Objects;

public class Rol {
    private Integer id;
    private String nombre;

    public Rol() {}

    public Rol(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Rol(String nombre) {
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rol rol1 = (Rol) o;
        return Objects.equals(getId(), rol1.getId()) && Objects.equals(getNombre(), rol1.getNombre());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNombre());
    }

    @Override
    public String toString() {
        return "Rol{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
