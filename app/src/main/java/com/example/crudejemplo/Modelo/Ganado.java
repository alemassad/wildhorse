package com.example.crudejemplo.Modelo;

public class Ganado {
    private int id;
    private String sexo;
    private String raza;
    private String dispositivo;
    private String vientre;
    private String temperatura;
    private String estado;

    public Ganado() {
    }

    public Ganado(int id, String sexo, String raza, String dispositivo, String vientre, String temperatura, String estado) {
        this.id = id;
        this.sexo = sexo;
        this.raza = raza;
        this.dispositivo = dispositivo;
        this.vientre = vientre;
        this.temperatura = temperatura;
        this.estado = estado;
    }

    @Override
    public String toString() {

        return String.format("%-8s %-8s %-18s %-28s", id, sexo, raza, estado );
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getVientre() {
        return vientre;
    }

    public void setVientre(String vientre) {
        this.vientre = vientre;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }
}
