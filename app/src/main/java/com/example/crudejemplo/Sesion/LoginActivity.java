package com.example.crudejemplo.Sesion;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crudejemplo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editPass;
    private ProgressBar progressBar;
    private FirebaseAuth authUser;
    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Login");

        editEmail = findViewById(R.id.editText_login_email);
        editPass = findViewById(R.id.editText_login_pass);
        progressBar = findViewById(R.id.progressBar_login);
        authUser = FirebaseAuth.getInstance();
        Button buttonForgot = findViewById(R.id.button_forgot_pass);
        buttonForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Ya puedes resetear tu password", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));

            }
        });

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

        //Login usuario
        Button buttonlogin = findViewById(R.id.button_login);
        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                String pass = editPass.getText().toString();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this, "Ingresar su Email", Toast.LENGTH_SHORT).show();
                    editEmail.setError("Se requiere el Email");
                    editEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(LoginActivity.this, "Re ingresar su Email", Toast.LENGTH_SHORT).show();
                    editEmail.setError("Se requiere un Email válido");
                    editEmail.requestFocus();
                } else if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(LoginActivity.this, "Ingresar su Contraseña", Toast.LENGTH_SHORT).show();
                    editEmail.setError("Se requiere una Contraseña");
                    editEmail.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(email, pass);
                }
            }
        });


        Button buttonregister = findViewById(R.id.button_register);
        buttonregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String pass) {
        authUser.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Estas Logeado", Toast.LENGTH_SHORT).show();

                    //Obtener instancia de usuario
                    FirebaseUser firebaseUser = authUser.getCurrentUser();

                    //Verificar si el email del usuario esta verificado para acceder al sistema
                    if (Objects.requireNonNull(firebaseUser).isEmailVerified()){
                        Toast.makeText(LoginActivity.this, "Ya estas logueado", Toast.LENGTH_SHORT).show();

                        //Abrir el perfil de usuario
                        //Comenzar el UserProfile Activity
                        startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
                        finish(); //Cerrar el LoginActivity
                    } else {
                        firebaseUser.sendEmailVerification();
                         authUser.signOut();
                         showAlertDialog();
                    }

                } else{
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthInvalidUserException e){
                        editEmail.setError("Usuario inexistente. Registrarse!!");
                        editEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        editEmail.setError("Credenciales de usuario invalidas!!. Reingresar nuevamente");
                        editEmail.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(LoginActivity.this, "Error al intentar loguearse", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        //Creamos una caja de dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email no verificado");
        builder.setMessage("Verificar su Email. No puedes loguerte sin un Email verificado");

        //Abrir correo si el usuario cliquea el BOTON de continuar
        builder.setPositiveButton("CONTINUAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //Para Abrir el eMail en una nueva ventana y no con nuestra APP
                startActivity(intent);
            }
        });

        //Crear La caja de alerta
        AlertDialog alertDialog = builder.create();

        //Mostrar la caja de alerta
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authUser.getCurrentUser() != null){
            //Toast.makeText(LoginActivity.this, "Ya estas logueado", Toast.LENGTH_SHORT).show();

            //Comenzar el UserProfile Activity
            startActivity(new Intent(LoginActivity.this,UserProfileActivity.class));
            finish(); //Cerrar el LoginActivity
        } else {
            Toast.makeText(LoginActivity.this, "Ya puedes loguerte!!", Toast.LENGTH_SHORT).show();
        }
    }
}