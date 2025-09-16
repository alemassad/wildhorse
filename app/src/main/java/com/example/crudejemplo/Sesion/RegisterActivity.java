package com.example.crudejemplo.Sesion;

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
import androidx.appcompat.app.AppCompatActivity;

import com.example.crudejemplo.Modelo.ReadWriteUserDetails;
import com.example.crudejemplo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextNombre;
    private EditText editTextApellido;
    private EditText editTextEmail;
    private EditText editTextTel;
    private EditText editTextpass;
    private EditText editTextRepass;
    private EditText editTextDni;
    private ProgressBar progressBar;
    private final String grupo = "peon";// Al registrarce el usuario se le asigna el grupo peon por defecto
    private static final String TAG = "RegisterActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Registrar");

        Toast.makeText(RegisterActivity.this, "Ya puedes registrarte", Toast.LENGTH_SHORT).show();


        editTextNombre = findViewById(R.id.txt_register_fullname);
        editTextApellido = findViewById(R.id.txt_register_subname);
        editTextEmail = findViewById(R.id.txt_register_email);
        editTextTel = findViewById(R.id.txt_register_tel);
        editTextpass = findViewById(R.id.txt_register_password);
        editTextRepass = findViewById(R.id.txt_register_repassword);
        editTextDni = findViewById(R.id.txt_register_dni);

        //Mostrar u ocultar pass en editText
        ImageView imageViewPass = findViewById(R.id.imgView_ver_pass);
        imageViewPass.setImageResource(R.drawable.ic_hide_pwd);
        imageViewPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextpass.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //Si el password esta visible, lo ocultamos
                    editTextpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Cambiamos el icono del password
                    imageViewPass.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    editTextpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewPass.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        //Mostrar u ocultar repass en editText
        ImageView imageViewRePass = findViewById(R.id.imgView_ver_repass);
        imageViewRePass.setImageResource(R.drawable.ic_hide_pwd);
        imageViewRePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextRepass.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //Si el password esta visible, lo ocultamos
                    editTextRepass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Cambiamos el icono del password
                    imageViewRePass.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    editTextRepass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewRePass.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });



        progressBar = findViewById(R.id.progressBar);

        Button buttonRegister = findViewById(R.id.btnRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dniReg = editTextDni.getText().toString();
                String nombreReg = editTextNombre.getText().toString();
                String apellidoReg = editTextApellido.getText().toString();
                String emailReg = editTextEmail.getText().toString();
                String telReg = editTextTel.getText().toString();
                String passReg = editTextpass.getText().toString();
                String repassReg = editTextRepass.getText().toString();





                //Validacion del telefono
                String regexTel = "[1-5][0-9]{9}"; //Primer numero no peden ser 6,7,8,9 y el resto puede ser cualquiera
                Matcher telMatcher;
                Pattern telPattern = Pattern.compile(regexTel);
                telMatcher = telPattern.matcher(telReg);

                if (TextUtils.isEmpty(dniReg)){
                    Toast.makeText(RegisterActivity.this, "Ingresar su Dni", Toast.LENGTH_SHORT).show();
                    editTextDni.setError("Dni requerido");
                    editTextDni.requestFocus();
                } else if (dniReg.length() < 8) {
                    Toast.makeText(RegisterActivity.this, "Reingresar su Dni", Toast.LENGTH_SHORT).show();
                    editTextDni.setError("El Dni debe tener al menos 8 cifras");
                    editTextDni.requestFocus();
                } else if (TextUtils.isEmpty(nombreReg)){
                    Toast.makeText(RegisterActivity.this, "Ingresar su nombre", Toast.LENGTH_SHORT).show();
                    editTextNombre.setError("Nombre requerido");
                    editTextNombre.requestFocus();
                } else if (TextUtils.isEmpty(apellidoReg)) {
                    Toast.makeText(RegisterActivity.this, "Ingresar su apellido", Toast.LENGTH_SHORT).show();
                    editTextApellido.setError("Apellido requerido");
                    editTextApellido.requestFocus();
                } else if (TextUtils.isEmpty(emailReg)) {
                    Toast.makeText(RegisterActivity.this, "Ingresar su eMail", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("eMail requerido");
                    editTextEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailReg).matches()) {
                    Toast.makeText(RegisterActivity.this, "Reingresar su Email", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Se requiere un Email valido requerido");
                    editTextEmail.requestFocus();
                } else if (TextUtils.isEmpty(telReg)) {
                    Toast.makeText(RegisterActivity.this, "Ingresar su telefono", Toast.LENGTH_SHORT).show();
                    editTextTel.setError("Telefono requerido");
                    editTextTel.requestFocus();
                }else if (telReg.length() < 10) {
                    Toast.makeText(RegisterActivity.this, "Reingresar su telefono", Toast.LENGTH_SHORT).show();
                    editTextTel.setError("El Telefono debe tener al menos 10 numeros");
                    editTextTel.requestFocus();
                } else if (!telMatcher.find()) {
                    Toast.makeText(RegisterActivity.this, "Reingresar su telefono", Toast.LENGTH_SHORT).show();
                    editTextTel.setError("Numero de Telefono invalido");
                    editTextTel.requestFocus();
                } else if (TextUtils.isEmpty(passReg)) {
                    Toast.makeText(RegisterActivity.this, "Ingresar su contraseña ", Toast.LENGTH_SHORT).show();
                    editTextpass.setError("Contraseña requerida");
                    editTextpass.requestFocus();
                } else if (passReg.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Su contraseña debe tener al menos 6 digitos", Toast.LENGTH_SHORT).show();
                    editTextpass.setError("Contraseña muy insegura");
                    editTextpass.requestFocus();
                } else if (TextUtils.isEmpty(repassReg)) {
                    Toast.makeText(RegisterActivity.this, "Reingresar su contraseña ", Toast.LENGTH_SHORT).show();
                    editTextRepass.setError("Confirmar la Contraseña requerida");
                    editTextRepass.requestFocus();
                } else if (!passReg.equals(repassReg)) {
                    Toast.makeText(RegisterActivity.this, "Las  contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    editTextRepass.setError("Verificar que las Contraseñas coincidan");
                    editTextRepass.requestFocus();
                    //limpiamos los campos password
                    editTextRepass.clearComposingText();
                    editTextpass.clearComposingText();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(dniReg, nombreReg, apellidoReg, emailReg, telReg, passReg);
                }
            }
        });

    }

    private void registerUser(String dniReg ,String editNombre, String editApellido, String editEmail, String editTel, String editpass) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        //Creamos un nuevo Usuario
        auth.createUserWithEmailAndPassword(editEmail, editpass).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    //Actualizamos la visualizacion del usuario
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(editNombre).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    //Ingresamos los datos del registro el la DB Realtime
                    ReadWriteUserDetails userDetails = new ReadWriteUserDetails(dniReg,editNombre,editApellido,editEmail,editTel,editpass,grupo);

                    //Conectamos una referrencia a la tabla user
                    DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Usuario");
                    referenceUser.child(firebaseUser.getUid()).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //Verificamos el email
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(RegisterActivity.this, "Registro exitoso!! Por favor verificar en tu Email", Toast.LENGTH_SHORT).show();

                                //Abrir UserProfile despues de la registracion
                                Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                       | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); //Cerramos RegisterActivity

                            } else {
                                Toast.makeText(RegisterActivity.this, "Registro Erroneo. Por favor intertar nuevamente!!", Toast.LENGTH_SHORT).show();

                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthWeakPasswordException e){
                        editTextpass.setError("Contraseña muy debil, combina con diferentes caracteres, numeros y caracteres especiales");
                        editTextpass.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        editTextEmail.setError("Email es inválido o ya está en uso");
                        editTextEmail.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e){
                        editTextEmail.setError("Usuario ya existente con este Email. Probar otro Email");
                        editTextEmail.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage() );
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

}