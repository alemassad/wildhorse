package com.example.crudejemplo.Modelo;

public class Grupo {

    private int idGrupo;
    private String nombreGrupo;
    private String permiso;

    public Grupo(String nombreGrupo, String permiso) {
       // this.idGrupo = idGrupo;
        this.nombreGrupo = nombreGrupo;
        this.permiso = permiso;
    }

    public Grupo(int idGrupo, String nombreGrupo, String permiso) {
        this.idGrupo = idGrupo;
        this.nombreGrupo = nombreGrupo;
        this.permiso = permiso;
    }

    public Grupo() {
    }

    @Override
    public String toString() {

        return String.format("%-18s", nombreGrupo);
    }

    public Grupo(String nombreGrupo) {
    }

    public String getPermiso() {
        return permiso;
    }

    public void setPermiso(String permiso) {
        this.permiso = permiso;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }
}
