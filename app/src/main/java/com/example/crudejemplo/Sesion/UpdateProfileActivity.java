package com.example.crudejemplo.Sesion;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crudejemplo.Modelo.ReadWriteUserDetails;
import com.example.crudejemplo.R;
import com.example.crudejemplo.Controladora.InicioActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText editTextDni, editTextUpNombre, editTextUpApe, editTextUpMail, editTextTel;
    private String txtNombre, txtApellido, txtEmail, txtTel, txtPass, txtDni, grupo;
    private FirebaseAuth authPerfil;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_profile);

        getSupportActionBar().setTitle("Actualizar perfil");

        progressBar = findViewById(R.id.progressBar);
        editTextUpNombre = findViewById(R.id.edit_update_perfil_nombre);
        editTextUpApe = findViewById(R.id.edit_update_perfil_ape);
        editTextTel = findViewById(R.id.edit_update_perfil_tel);
        editTextDni = findViewById(R.id.edit_update_perfil_dni);
        authPerfil = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authPerfil.getCurrentUser();

        //Mostrar los datos de perfil

        mostrarPerfil(firebaseUser);


        //Actualizar Foto de perfil
        Button btnUploadFotoPerfil = findViewById(R.id.botton_upload_perfil_foto);
        btnUploadFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this, UploadProfilePicActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Actualizar Email
        Button btnUploadMail = findViewById(R.id.botton_upload_perfil_mail);
        btnUploadMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this, UpdateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Actualizar Perfil
        Button btnActualizaPerfil = findViewById(R.id.botton_upload_perfil);
        btnActualizaPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActualizarPerfil(firebaseUser);
            }
        });

    }

    //Metodo Actualiza Perfil de Usuario
    private void ActualizarPerfil(FirebaseUser firebaseUser) {

        // Tomamos los datos ingresado para actualizar
        txtDni = editTextDni.getText().toString();
        txtNombre = editTextUpNombre.getText().toString();
        txtApellido = editTextUpApe.getText().toString();
        txtTel = editTextTel.getText().toString();


        //Validacion del telefono
        String regexTel = "[1-5][0-9]{9}"; //Primer numero no peden ser 6,7,8,9 y el resto puede ser cualquiera
        Matcher telMatcher;
        Pattern telPattern = Pattern.compile(regexTel);
        telMatcher = telPattern.matcher(txtTel);

        if (TextUtils.isEmpty(txtDni)) {
            Toast.makeText(UpdateProfileActivity.this, "Ingresar su Dni", Toast.LENGTH_SHORT).show();
            editTextDni.setError("Dni requerido");
            editTextDni.requestFocus();}
        else if (txtDni.length() < 8){
            Toast.makeText(UpdateProfileActivity.this, "Reingresar su Dni", Toast.LENGTH_SHORT).show();
            editTextDni.setError("El Dni debe tener al menos 8 cifras");
            editTextDni.requestFocus();
        } else if (TextUtils.isEmpty(txtNombre)) {
            Toast.makeText(UpdateProfileActivity.this, "Ingresar su nombre", Toast.LENGTH_SHORT).show();
            editTextUpNombre.setError("Nombre requerido");
            editTextUpNombre.requestFocus();
        } else if (TextUtils.isEmpty(txtApellido)) {
            Toast.makeText(UpdateProfileActivity.this, "Ingresar su apellido", Toast.LENGTH_SHORT).show();
            editTextUpApe.setError("Apellido requerido");
            editTextUpApe.requestFocus();
        } else if (TextUtils.isEmpty(txtTel)) {
            Toast.makeText(UpdateProfileActivity.this, "Ingresar su telefono", Toast.LENGTH_SHORT).show();
            editTextTel.setError("Telefono requerido");
            editTextTel.requestFocus();
        } else if (txtTel.length() < 10) {
            Toast.makeText(UpdateProfileActivity.this, "Reingresar su telefono", Toast.LENGTH_SHORT).show();
            editTextTel.setError("El Telefono debe tener al menos 10 numeros");
            editTextTel.requestFocus();
        } else if (!telMatcher.find()) {
            Toast.makeText(UpdateProfileActivity.this, "Reingresar su telefono", Toast.LENGTH_SHORT).show();
            editTextTel.setError("Numero de Telefono invalido");
            editTextTel.requestFocus();
        } else {

            //Subimos los datos nuevos para actualizar en la DB
            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(txtDni, txtNombre, txtApellido, txtEmail, txtTel, txtPass, grupo);

            //Traemos Referencia de usuario de la DB userregisdted

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuario");

            String userId = firebaseUser.getUid();
            progressBar.setVisibility(View.VISIBLE);
            reference.child(userId).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(txtNombre).build();
                        firebaseUser.updateProfile(profileUpdate);

                        Toast.makeText(UpdateProfileActivity.this, "Actualizacion exitosa", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(UpdateProfileActivity.this, UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });

        }
    }

    private void mostrarPerfil(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //Referenciamos los usuarios de la DB
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuario");

        progressBar.setVisibility(View.VISIBLE);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readWriteUserDetails != null) {
                    txtNombre = firebaseUser.getDisplayName();
                    txtApellido = readWriteUserDetails.apellido;
                    txtTel = readWriteUserDetails.tel;
                    txtDni = readWriteUserDetails.dni;
                    txtEmail = readWriteUserDetails.email;
                    txtPass = readWriteUserDetails.pass;
                    grupo = readWriteUserDetails.grupo;

                    editTextUpNombre.setText(txtNombre);
                    editTextUpApe.setText(txtApellido);
                    editTextTel.setText(txtTel);
                    editTextDni.setText(txtDni);
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "No se pudo encontrar en la Base Datos", Toast.LENGTH_SHORT).show();

                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "No se pudo buscar en la Base Datos", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            }
        });
    }
    //Crear Accion Barra del menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //accion de seleccionar un item del menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_refresh) {

            //Refresh Activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(UpdateProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(UpdateProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_mapa) {
            Intent intent = new Intent(UpdateProfileActivity.this, InicioActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UpdateProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UpdateProfileActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_logout) {
            authPerfil.signOut();
            Toast.makeText(UpdateProfileActivity.this, "Sesión Cerrrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateProfileActivity.this, InicioActivity.class);
            //Limpiar form, por si el ususario preciona la tecla volver para ver los datos anteriores
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Cerramos UserProfileActivity
        } else {
            Toast.makeText(
                    UpdateProfileActivity.this, "Ocurrió un error!!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}