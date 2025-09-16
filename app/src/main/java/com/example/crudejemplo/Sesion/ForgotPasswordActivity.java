package com.example.crudejemplo.Sesion;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crudejemplo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText editResetPass;
    private Button buttonResetPass;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private final static String TAG = "ForgotPasswordActivity";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().setTitle("Recuperar contraseña");

        editResetPass = findViewById(R.id.editText_pass_reset_mail);
        buttonResetPass = findViewById(R.id.button_forgot_reset);
        progressBar = findViewById(R.id.progressBar);

        buttonResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editResetPass.getText().toString();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotPasswordActivity.this, "Ingrese su Email registrado", Toast.LENGTH_SHORT).show();
                    editResetPass.setError("Se requiere Email");
                    editResetPass.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ForgotPasswordActivity.this, "Ingresar un Email valido", Toast.LENGTH_SHORT).show();
                    editResetPass.setError("Se requiere un Email valido");
                    editResetPass.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword(email);
                }
            }
        });

    }

    private void resetPassword(String email) {

        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this, "Veririfar en su casilla de correo el Link de reseteo del password", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    //Limpiar form, por si el ususario preciona la tecla volver para ver los datos anteriores
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Cerramos UserProfileActivity
                } else {
                    try {
                        throw task.getException();

                    } catch (FirebaseAuthInvalidUserException e){
                        editResetPass.setError("Usuario no existente. Registrarse nuevamente");
                        editResetPass.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                   // Toast.makeText(ForgotPasswordActivity.this, "Error al intentar el reseteo", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}