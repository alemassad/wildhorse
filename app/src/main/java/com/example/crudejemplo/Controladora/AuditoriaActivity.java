package com.example.crudejemplo.Controladora;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudejemplo.R;
import com.example.crudejemplo.databinding.ActivityAuditoriaBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuditoriaActivity extends AppCompatActivity {
    ActivityAuditoriaBinding activityBinding;
    private EditText editTextUsuario, editTextFecha;
    private Button btnBuscar;
    private RecyclerView recyclerViewAuditoria;
    private AuditoriaAdapter auditoriaAdapter;
    private List<Map<String, String>> listaAuditoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = ActivityAuditoriaBinding.inflate(getLayoutInflater());
        setContentView(activityBinding.getRoot());
       // setContentView(R.layout.activity_auditoria);

        // Inicializar vistas
        editTextUsuario = findViewById(R.id.editTextUsuario);
        editTextFecha = findViewById(R.id.editTextFecha);
        btnBuscar = findViewById(R.id.btnBuscar);
        recyclerViewAuditoria = findViewById(R.id.recyclerViewAuditoria);

        // Configurar RecyclerView
        listaAuditoria = new ArrayList<>();
        auditoriaAdapter = new AuditoriaAdapter(listaAuditoria);
        recyclerViewAuditoria.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAuditoria.setAdapter(auditoriaAdapter);

        // Configurar el botón de búsqueda
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarAuditoria();
            }
        });
    }

    private void buscarAuditoria() {
        String usuario = editTextUsuario.getText().toString().trim();
        String fecha = editTextFecha.getText().toString().trim();

        // Obtener referencia a la tabla "auditoria" en Firebase
        DatabaseReference refAuditoria = FirebaseDatabase.getInstance().getReference("Auditoria");

        // Realizar la búsqueda
        refAuditoria.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaAuditoria.clear(); // Limpiar la lista anterior

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<String, String> auditoria = (Map<String, String>) dataSnapshot.getValue();

                    // Filtrar por usuario y fecha
                    if ((usuario.isEmpty() || auditoria.get("usuario").equalsIgnoreCase(usuario)) &&
                            (fecha.isEmpty() || auditoria.get("fechaHora").contains(fecha))) {
                        listaAuditoria.add(auditoria);
                    }
                }

                // Notificar al adapter que los datos han cambiado
                auditoriaAdapter.notifyDataSetChanged();

                if (listaAuditoria.isEmpty()) {
                    Toast.makeText(AuditoriaActivity.this, "No se encontraron registros", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AuditoriaActivity", "Error al obtener datos de Firebase", error.toException());
                Toast.makeText(AuditoriaActivity.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}