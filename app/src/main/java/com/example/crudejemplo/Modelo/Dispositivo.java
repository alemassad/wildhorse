package com.example.crudejemplo.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class Dispositivo implements Parcelable {
    private int idDispositivo;
    private String marca;
    private String descripcion;
    private String estado;

    public Dispositivo(String descripcionDispositivo) {
        this.descripcion = descripcionDispositivo;
    }

    @Override
    public String toString() {

        return String.format("%-8s %-8s %-18s %-28s", idDispositivo, marca, descripcion, estado);
    }

    public Dispositivo(int idDispositivo, String marca, String descripcion, String estado) {
        this.idDispositivo = idDispositivo;
        this.marca = marca;
        this.descripcion = descripcion;
        this.estado = estado;
    }
    public Dispositivo() {
    }
    public int getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(int idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.idDispositivo);
        dest.writeString(this.marca);
        dest.writeString(this.descripcion);
        dest.writeString(this.estado);
    }

    public void readFromParcel(Parcel source) {
        this.idDispositivo = source.readInt();
        this.marca = source.readString();
        this.descripcion = source.readString();
        this.estado = source.readString();
    }

    protected Dispositivo(Parcel in) {
        this.idDispositivo = in.readInt();
        this.marca = in.readString();
        this.descripcion = in.readString();
        this.estado = in.readString();
    }

    public static final Parcelable.Creator<Dispositivo> CREATOR = new Parcelable.Creator<Dispositivo>() {
        @Override
        public Dispositivo createFromParcel(Parcel source) {
            return new Dispositivo(source);
        }

        @Override
        public Dispositivo[] newArray(int size) {
            return new Dispositivo[size];
        }
    };
}
