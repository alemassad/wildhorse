package com.example.crudejemplo.Controladora;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AuditoriaUtil {

    // Método estático para registrar una acción en la auditoría
    public static void registrarAccion(String accion) {
        // Obtener el usuario actual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String usuarioActual = (user != null && user.getEmail() != null) ? user.getEmail() : "Anonimo";

        // Obtener la fecha y hora actual
        String fechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        // Crear un objeto para almacenar los datos de auditoría
        Map<String, Object> auditoria = new HashMap<>();
        auditoria.put("usuario", usuarioActual); // Usar el usuario actual
        auditoria.put("accion", accion);
        auditoria.put("fechaHora", fechaHora);

        // Guardar en la tabla "Auditoria" de Firebase
        DatabaseReference refAuditoria = FirebaseDatabase.getInstance().getReference("Auditoria");
        refAuditoria.push().setValue(auditoria)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Auditoria", "Acción registrada: " + accion + "Fecha y hora: " + fechaHora + "Usuario: " + usuarioActual);
                })
                .addOnFailureListener(e -> {
                    Log.e("Auditoria", "Error al registrar acción: " + e.getMessage());
                });
    }
}