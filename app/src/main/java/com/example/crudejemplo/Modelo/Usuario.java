package com.example.crudejemplo.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class Usuario implements Parcelable {
    //private static int contadorId = 0; // Contador estático para el ID
    private String dni;
    private String nombre;
    private String apellido;
    private String email;
    private String tel;
    private String grupo;

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }


    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Usuario(String dni, String nombre, String apellido, String email, String tel, String grupo) {

        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.tel = tel;
        this.grupo = grupo;
    }


    public Usuario(String dni, String nombre, String apellido, String email, String telefono) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.tel = telefono;

    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Usuario() {
    }

    @Override

        public String toString() {
            return String.format("%-8s %-18s %-18s %-8s", dni, nombre, apellido, grupo);
        }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.dni);
        dest.writeString(this.nombre);
    }

    public void readFromParcel(Parcel source) {
        this.dni = source.readString();
        this.nombre = source.readString();
    }

    protected Usuario(Parcel in) {
        this.dni = in.readString();
        this.nombre = in.readString();
    }

    public static final Parcelable.Creator<Usuario> CREATOR = new Parcelable.Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel source) {
            return new Usuario(source);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };
}
