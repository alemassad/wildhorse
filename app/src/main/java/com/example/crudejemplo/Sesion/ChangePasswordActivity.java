package com.example.crudejemplo.Sesion;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.crudejemplo.R;
import com.example.crudejemplo.Controladora.InicioActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText editPassActual, editPassNuevo, editPassCpnfirmarNuevo;
    private TextView txtAutentificado;
    private Button btnCambiaPass, btnReAutentifica;
    private ProgressBar progressBar;
    private String userPassActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setTitle("Cambiar Contraseña");

        editPassActual = findViewById(R.id.edit_pass_actual);
        editPassNuevo = findViewById(R.id.edit_change_pass_nuevo);
        editPassCpnfirmarNuevo = findViewById(R.id.edit_change_pass_nuevo_confirmar);
        txtAutentificado = findViewById(R.id.textView_change_pass_autenti);
        progressBar = findViewById(R.id.progressBar);
        btnReAutentifica = findViewById(R.id.btn_change_pass_autenti);
        btnCambiaPass = findViewById(R.id.btn_change_pass_confirmar);


        //Mostrar u ocultar pass actual en editText
        ImageView imageViewPassActual = findViewById(R.id.imgView_ver_pass);
        imageViewPassActual.setImageResource(R.drawable.ic_hide_pwd);
        imageViewPassActual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editPassActual.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //Si el password esta visible, lo ocultamos
                    editPassActual.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Cambiamos el icono del password
                    imageViewPassActual.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    editPassActual.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewPassActual.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        //Mostrar u ocultar pass nuevo en editText
        ImageView imageViewPassNuevo = findViewById(R.id.imgView_ver_pass_nuevo);
        imageViewPassNuevo.setImageResource(R.drawable.ic_hide_pwd);
        imageViewPassNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editPassNuevo.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //Si el password esta visible, lo ocultamos
                    editPassNuevo.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Cambiamos el icono del password
                    imageViewPassNuevo.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    editPassNuevo.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewPassNuevo.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        //Mostrar u ocultar confirmar pass nuevo en editText
        ImageView imageViewConfirmarPassNuevo = findViewById(R.id.imgView_ver_pass_confirmar);
        imageViewConfirmarPassNuevo.setImageResource(R.drawable.ic_hide_pwd);
        imageViewConfirmarPassNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editPassCpnfirmarNuevo.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //Si el password esta visible, lo ocultamos
                    editPassCpnfirmarNuevo.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Cambiamos el icono del password
                    imageViewConfirmarPassNuevo.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    editPassCpnfirmarNuevo.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewConfirmarPassNuevo.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });







        // Desabilitar Editext de la nueva contraseña, confirmar la nueva contraseña y al boton de cambiar contraseña

        editPassNuevo.setEnabled(false);
        editPassCpnfirmarNuevo.setEnabled(false);
        btnCambiaPass.setEnabled(false);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser =firebaseAuth.getCurrentUser();

        if (firebaseUser.equals("")){
            Toast.makeText(ChangePasswordActivity.this, "No se ha encontrado usuario en la DB", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            reAutentificarUsuario(firebaseUser);

        }
    }
    //Se debe autentificar el usuario antes de hacer cambios de contraseña
    private void reAutentificarUsuario(FirebaseUser firebaseUser) {
        btnReAutentifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPassActual = editPassActual.getText().toString();

                if (TextUtils.isEmpty(userPassActual)){
                    Toast.makeText(ChangePasswordActivity.this, "Es neceesario una contraseña", Toast.LENGTH_SHORT).show();
                    editPassActual.setError("Ingresar su contraseña actual para autentificarte");
                    editPassActual.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    //reAutentificamos el usuario
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPassActual);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);

                                // Desabilitamos Editext de la "contraseña actual". Abilitamos Editext de la "nueva contraseña" y de "Confirmar nueva"
                                editPassActual.setEnabled(false);
                                editPassNuevo.setEnabled(true);
                                editPassCpnfirmarNuevo.setEnabled(true);

                                //Abilitamos el boton de "cambiar contraseña". Y desabilitamos el boton "Autentificar"
                                btnReAutentifica.setEnabled(false);
                                btnCambiaPass.setEnabled(true);

                                // Ponemos en el TextView al usuario autentificado
                                txtAutentificado.setText("Estas autentificado"+" Ya puedes cambiar tu contraseña");

                                //Actualizamos el color del boton "Cambiar la contraseña"
                                btnCambiaPass.setBackgroundTintList(ContextCompat.getColorStateList(ChangePasswordActivity.this, R.color.verde));

                                btnCambiaPass.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cambiarContrasena(firebaseUser);
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e){
                                    Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void cambiarContrasena(FirebaseUser firebaseUser) {
        String userPassNuevo = editPassNuevo.getText().toString();
        String userPassConfirma = editPassCpnfirmarNuevo.getText().toString();
        
        if (TextUtils.isEmpty(userPassNuevo)){
            Toast.makeText(ChangePasswordActivity.this, "es necesario una nueva contraseña", Toast.LENGTH_SHORT).show();
            editPassNuevo.setError("Ingrese una nueva contraseña");
            editPassNuevo.requestFocus();
        } else if (TextUtils.isEmpty(userPassConfirma)){
            Toast.makeText(ChangePasswordActivity.this, "es necesario que confirmes la nueva contraseña", Toast.LENGTH_SHORT).show();
            editPassNuevo.setError("Reingrese la nueva contraseña");
            editPassNuevo.requestFocus();
        } else if (!userPassNuevo.matches(userPassConfirma)) {
            Toast.makeText(ChangePasswordActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            editPassNuevo.setError("Reingrese la misma contraseña");
            editPassNuevo.requestFocus();
        } else if (userPassActual.matches(userPassNuevo)) {
            Toast.makeText(ChangePasswordActivity.this, "La nueva contraseña no debe ser igual a la antigua", Toast.LENGTH_SHORT).show();
            editPassNuevo.setError("Ingrese una nueva contraseña");
            editPassNuevo.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userPassNuevo).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ChangePasswordActivity.this, "La contraseña se ha cambiado", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e){
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

    }

    //Crear Accion Barra del menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.common_menu, menu );
        return  super.onCreateOptionsMenu(menu);
    }
    //accion de seleccionar un item del menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();

        if (id == R.id.menu_refresh){

            //Refresh Activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_mapa) {
            Intent intent = new Intent(ChangePasswordActivity.this, InicioActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(ChangePasswordActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent( ChangePasswordActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_logout) {
            firebaseAuth.signOut();
            Toast.makeText(ChangePasswordActivity.this, "Sesión Cerrrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, InicioActivity.class);
            //Limpiar form, por si el ususario preciona la tecla volver para ver los datos anteriores
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Cerramos UserProfileActivity
        } else {
            Toast.makeText(ChangePasswordActivity.this, "Ocurrió un error!!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}