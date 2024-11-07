package com.martin.gestortickets.entities;

import java.io.Serializable;
import java.util.Objects;

public class Usuario implements Serializable {
    private Integer id;
    private String password;
    private Rol rol;
    boolean bloqueado;
    Integer fallas;
    Integer marcas;

    public Usuario() {}

    public Usuario(Integer id, String password, Rol rol, boolean bloqueado, Integer fallas, Integer marcas) {
        this.id = id;
        this.password = password;
        this.rol = rol;
        this.bloqueado = bloqueado;
        this.fallas = fallas;
        this.marcas = marcas;
    }

    public Usuario(Integer id, Rol rol, boolean bloqueado, Integer fallas, Integer marcas) {
        this.id = id;
        this.rol = rol;
        this.bloqueado = bloqueado;
        this.fallas = fallas;
        this.marcas = marcas;
    }

    public Usuario(String password, Rol rol) {
        this.password = password;
        this.rol = rol;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public Integer getFallas() {
        return fallas;
    }

    public void setFallas(Integer fallas) {
        this.fallas = fallas;
    }

    public Integer getMarcas() {
        return marcas;
    }

    public void setMarcas(Integer marcas) {
        this.marcas = marcas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return isBloqueado() == usuario.isBloqueado() && Objects.equals(getId(), usuario.getId()) && Objects.equals(getPassword(), usuario.getPassword()) && Objects.equals(getRol(), usuario.getRol()) && Objects.equals(getFallas(), usuario.getFallas()) && Objects.equals(getMarcas(), usuario.getMarcas());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPassword(), getRol(), isBloqueado(), getFallas(), getMarcas());
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", rol=" + rol +
                ", bloqueado=" + bloqueado +
                ", fallas=" + fallas +
                ", marcas=" + marcas +
                '}';
    }
}
