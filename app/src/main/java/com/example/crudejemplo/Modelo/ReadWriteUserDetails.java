package com.example.crudejemplo.Modelo;

public class ReadWriteUserDetails {

    public ReadWriteUserDetails() {
        this("", "", "", "", "");
    }

    public String dni, nombre, apellido, email, tel, pass, grupo;
       
    public ReadWriteUserDetails(String txtDni,String txtNombre,String txtApellido,String txtEmail,String txtTel) {
        this.dni = txtDni;
        this.nombre = txtNombre;
        this.apellido = txtApellido;
        this.email = txtEmail;
        this.tel = txtTel;
    }
    public ReadWriteUserDetails(String dni ,String txtNombre, String txtApellido, String txtEmail, String txtTel, String txtPass, String grupo) {

        this.dni = dni;
        this.nombre = txtNombre;
        this.apellido = txtApellido;
        this.email = txtEmail;
        this.tel = txtTel;
        this.pass = txtPass;
        this.grupo = grupo;
    }
}
