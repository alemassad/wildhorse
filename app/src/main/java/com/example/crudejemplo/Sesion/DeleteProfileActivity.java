package com.example.crudejemplo.Sesion;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.crudejemplo.R;
import com.example.crudejemplo.Controladora.InicioActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private EditText editUserPass;
    private TextView txtAutentificado;
    private ProgressBar progressBar;
    private String userPass;
    private Button btnReautentifica, btnDelUsuario;
    private static final String TAG = "DeleteProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_profile);

        getSupportActionBar().setTitle("Borrar perfil");

        progressBar = findViewById(R.id.progressBar);
        editUserPass = findViewById(R.id.edit_delete_usuario_pass);
        txtAutentificado = findViewById(R.id.textView_delete_usuario_autenti);
        btnReautentifica = findViewById(R.id.btn_delete_usuario_autenti);
        btnDelUsuario = findViewById(R.id.btn_delete_usuario);

        //Mostrar u ocultar pass en editText
        ImageView imageViewPass = findViewById(R.id.imgView_ver_pass);
        imageViewPass.setImageResource(R.drawable.ic_hide_pwd);
        imageViewPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editUserPass.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //Si el password esta visible, lo ocultamos
                    editUserPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Cambiamos el icono del password
                    imageViewPass.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    editUserPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewPass.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        // Desactivar el Boton de "Borrar perfil" hasta que se autentifique el usuario
        btnDelUsuario.setEnabled(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser.equals("")){
            Toast.makeText(DeleteProfileActivity.this, "Error al autentificar el usuario en la DB", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeleteProfileActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            reAutentificar(firebaseUser);
        }

    }

    //Se debe autentificar el usuario antes de poder borrar
    private void reAutentificar(FirebaseUser firebaseUser) {
        btnReautentifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPass = editUserPass.getText().toString();

                if (TextUtils.isEmpty(userPass)){
                    Toast.makeText(DeleteProfileActivity.this, "Es necesario una contraseña", Toast.LENGTH_SHORT).show();
                    editUserPass.setError("Ingresar su contraseña actual para autentificarte");
                    editUserPass.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    //reAutentificamos el usuario
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPass);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);

                                // Desabilitamos Editext de la "contraseña"
                                editUserPass.setEnabled(false);

                                //Abilitamos el boton de "BORRAR". Y desabilitamos el boton "Autentificar"
                                btnReautentifica.setEnabled(false);
                                btnDelUsuario.setEnabled(true);

                                // Ponemos en el TextView al usuario autentificado
                                txtAutentificado.setText("Estas autentificado"+" Ya puedes Borrar tu perfil. Atencion!!");

                                //Actualizamos el color del boton "Cambiar la contraseña"
                                btnDelUsuario.setBackgroundTintList(ContextCompat.getColorStateList(DeleteProfileActivity.this, R.color.verde));

                                btnDelUsuario.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v){
                                        mostrarAlerta();
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e){
                                    Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void mostrarAlerta() {
        //Creamos una caja de dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteProfileActivity.this);
        builder.setTitle("Quiere borrar el perfil con sus datos?");
        builder.setMessage("Atencion!! Estas por borrar todos los datos y el perfil. Esta accion es irreversible!");

        //Abrir correo si el usuario cliquea el BOTON de continuar
        builder.setPositiveButton("CONTINUAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                borrarDatosUsuario(firebaseUser);
            }
        });
        // Volver al UserProfileActivity si el usuario presiona "Cancelar"
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(DeleteProfileActivity.this, UserProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //Crear La caja de alerta
        AlertDialog alertDialog = builder.create();

        // Cambiar al color rojo el boton de Borrar
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.rojo));
            }
        });

        //Mostrar la caja de alerta
        alertDialog.show();
    }

    private void borrarUsuario() {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    firebaseAuth.signOut();
                    Toast.makeText(DeleteProfileActivity.this, "El perfil de usuario ha sido borrado", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DeleteProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e){
                        Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Borrar todos los datos del usuario
    private void borrarDatosUsuario(FirebaseUser firebaseUser) {
        //borrar foto de perfil
        if (firebaseUser.getPhotoUrl() != null){ // vemos si hay fotos cargadas antes de borrar
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReferenceFromUrl(firebaseUser.getPhotoUrl().toString());
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(TAG, "OnSuccess: Photo Deleted");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, e.getMessage());
                    Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }

        //Borrar Datos del Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuario");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "OnSuccess: User Data Deleted");

                borrarUsuario();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
                Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(DeleteProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(DeleteProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_mapa) {
            Intent intent = new Intent(DeleteProfileActivity.this, InicioActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(DeleteProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent( DeleteProfileActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_logout) {
            firebaseAuth.signOut();
            Toast.makeText(DeleteProfileActivity.this, "Sesión Cerrrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeleteProfileActivity.this, InicioActivity.class);
            //Limpiar form, por si el ususario preciona la tecla volver para ver los datos anteriores
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Cerramos UserProfileActivity
        } else {
            Toast.makeText(
                    DeleteProfileActivity.this, "Ocurrió un error!!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}