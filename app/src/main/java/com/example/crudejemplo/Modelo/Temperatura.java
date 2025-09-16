package com.example.crudejemplo.Modelo;

public class Temperatura {
    private int idSensor;
    private double temperaturaActual;
    private double temperaturaMax;
    private double temperaturaMin;
    private double rangoTemperatura;
    private String estado;

    @Override
    public String toString() {

        return String.format("%-18s %-18s %-10s", idSensor, temperaturaActual, estado);
    }

    public Temperatura() {
    }

    public Temperatura(int idSensor, double temperaturaActual, double temperaturaMax, double temperaturaMin, double rangoTemperatura, String estado) {
        this.idSensor = idSensor;
        this.temperaturaActual = temperaturaActual;
        this.temperaturaMax = temperaturaMax;
        this.temperaturaMin = temperaturaMin;
        this.rangoTemperatura = rangoTemperatura;
        this.estado = estado;
    }

    public int getIdSensor() {
        return idSensor;
    }

    public void setIdSensor(int idSensor) {
        this.idSensor = idSensor;
    }

    public double getTemperaturaActual() {
        return temperaturaActual;
    }

    public void setTemperaturaActual(double temperaturaActual) {
        this.temperaturaActual = temperaturaActual;
    }

    public double getTemperaturaMax() {
        return temperaturaMax;
    }

    public void setTemperaturaMax(double temperaturaMax) {
        this.temperaturaMax = temperaturaMax;
    }

    public double getTemperaturaMin() {
        return temperaturaMin;
    }

    public void setTemperaturaMin(double temperaturaMin) {
        this.temperaturaMin = temperaturaMin;
    }

    public double getRangoTemperatura() {
        return rangoTemperatura;
    }

    public void setRangoTemperatura(double rangoTemperatura) {
        this.rangoTemperatura = rangoTemperatura;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
