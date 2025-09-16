package com.example.crudejemplo.Modelo;

public class Raza {
    public int idRaza;
    public String nombre;
    public String pelaje;

    public Raza(String nombreRaza) {
        this.nombre = nombreRaza;
    }


    @Override
    public String toString() {

        return String.format("%-18s", nombre);
    }

    public Raza(int idRaza, String nombre, String pelaje) {
        this.idRaza = idRaza;
        this.nombre = nombre;
        this.pelaje = pelaje;
    }

    public Raza() {
    }

    public int getIdRaza() {
        return idRaza;
    }

    public void setIdRaza(int idRaza) {
        this.idRaza = idRaza;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPelaje() {
        return pelaje;
    }

    public void setPelaje(String pelaje) {
        this.pelaje = pelaje;
    }
}
