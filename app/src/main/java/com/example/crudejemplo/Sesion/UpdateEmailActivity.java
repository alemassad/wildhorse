package com.example.crudejemplo.Sesion;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
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

public class UpdateEmailActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView txtAutentificado;
    private String emailViejo, emailNuevo, userPass;
    private Button btnActualizaEmail;
    private EditText editEmailNuevo, editPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_email);

        getSupportActionBar().setTitle("Actualizar Email");

        progressBar = findViewById(R.id.progressBar);
        editPass = findViewById(R.id.edit_up_email_Verifica__pass);
        editEmailNuevo = findViewById(R.id.edit_up_email_nuevo);
        txtAutentificado = findViewById(R.id.textView_update_email_autenti);
        btnActualizaEmail = findViewById(R.id.btn_up_email);

        //Mostrar u ocultar pass en editText
        ImageView imageViewPass = findViewById(R.id.imgView_ver_pass);
        imageViewPass.setImageResource(R.drawable.ic_hide_pwd);
        imageViewPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editPass.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //Si el password esta visible, lo ocultamos
                    editPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Cambiamos el icono del password
                    imageViewPass.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    editPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewPass.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });


        btnActualizaEmail.setEnabled(false);    //Boton desactivado hasta que el usuario se autentifique
        editEmailNuevo.setEnabled(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Ponemos el viejo Email en la etiqueta
        emailViejo = firebaseUser.getEmail();
        TextView textViewEmailViejo = findViewById(R.id.textView_update_email_old);
        textViewEmailViejo.setText(emailViejo);
        if (firebaseUser.equals("")){
            Toast.makeText(UpdateEmailActivity.this, "No se ha encontrado un email en la DB", Toast.LENGTH_SHORT).show();
        } else {
            reAutentificar(firebaseUser);
        }

    }

    // reAutentificamos o verificamos el usuario antes de actualizar el Email
    private void reAutentificar(FirebaseUser firebaseUser) {
        Button btnVerificaUsuario = findViewById(R.id.btn_usuario_autenti);
        btnVerificaUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //traemos la Pass de autentificacion
                userPass = editPass.getText().toString();
                
                if (TextUtils.isEmpty(userPass)){
                    Toast.makeText(UpdateEmailActivity.this, "es necesaria la Contraseña para continuar", Toast.LENGTH_SHORT).show();
                    editPass.setError("Ingresar la Contraseña para autentificar");
                    editPass.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    AuthCredential credential = EmailAuthProvider.getCredential(emailViejo, userPass);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);

                                Toast.makeText(UpdateEmailActivity.this, "Contraseña verificada!!", Toast.LENGTH_SHORT).show();

                                txtAutentificado.setText("Autentificacion exitosa. Ya puedes Actualizar tu Email");

                                // Desabilitar Editext del Password y abilitar Editext del nuevoEmail
                                editPass.setEnabled(true);
                                editEmailNuevo.setEnabled(true);
                                btnVerificaUsuario.setEnabled(false);
                                btnActualizaEmail.setEnabled(true);

                                //Cambioar color del boton Actualizar email
                                btnActualizaEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this,
                                        R.color.verde));

                                btnActualizaEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        emailNuevo = editEmailNuevo.getText().toString();

                                        if (TextUtils.isEmpty(emailNuevo)){
                                            Toast.makeText(UpdateEmailActivity.this, "Se requiere el nuevo Email", Toast.LENGTH_SHORT).show();
                                            editEmailNuevo.setError("Ingrese el nuevo Email");
                                            editEmailNuevo.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailNuevo).matches()) {
                                            Toast.makeText(UpdateEmailActivity.this, "Se requiere un Email valido", Toast.LENGTH_SHORT).show();
                                            editEmailNuevo.setError("Poner un Email valido");
                                            editEmailNuevo.requestFocus();
                                        } else if (emailViejo.matches(emailNuevo)){
                                            Toast.makeText(UpdateEmailActivity.this, "El nuevo Email no debe ser igual al nuevo", Toast.LENGTH_SHORT).show();
                                            editEmailNuevo.setError("Ingresar un nuevo Email");
                                            editEmailNuevo.requestFocus();
                                        } else {
                                            progressBar.setVisibility(View.VISIBLE);

                                            ActualizarEmail(firebaseUser);
                                        }
                                    }
                                });

                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e){
                                    Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }

                        }
                    });
                }

            }
        });
    }

    private void ActualizarEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(emailNuevo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){

                    // verificamos el email
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(UpdateEmailActivity.this, "El Email ha sido Actualizado!! Verifica tu nuevo email", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateEmailActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e){
                        Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
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
            Intent intent = new Intent(UpdateEmailActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(UpdateEmailActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_mapa) {
            Intent intent = new Intent(UpdateEmailActivity.this, InicioActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UpdateEmailActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent( UpdateEmailActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_logout) {
            firebaseAuth.signOut();
            Toast.makeText(UpdateEmailActivity.this, "Sesión Cerrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateEmailActivity.this, InicioActivity.class);
            //Limpiar form, por si el ususario preciona la tecla volver para ver los datos anteriores
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Cerramos UserProfileActivity
        } else {
            Toast.makeText(UpdateEmailActivity.this, "Ocurrió un error!!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}