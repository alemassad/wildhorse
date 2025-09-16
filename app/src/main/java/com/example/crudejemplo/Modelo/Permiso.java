package com.example.crudejemplo.Modelo;

public class Permiso{
    private int id;
    private String accion;
    private String desc;


    @Override
    public String toString() {

        return String.format("%-18s", accion);
    }

    public Permiso() {
    }

    public Permiso(int id, String accion, String desc) {
        this.id = id;
        this.accion = accion;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
