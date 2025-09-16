package com.example.crudejemplo.Sesion;

import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.crudejemplo.Modelo.ReadWriteUserDetails;
import com.example.crudejemplo.R;
import com.example.crudejemplo.Controladora.InicioActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    private TextView txtVienvenido, txtNombre, txtApellido, txtEmail, txtTel;
    private String nombre, apellido, email, tel, grupo;
    private ProgressBar progressBar;
    private ImageView imageView;
    private FirebaseAuth firebaseAuth;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportActionBar().setTitle("Perfil de usuario");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        refreshSwipe();

        txtVienvenido = findViewById(R.id.textView_vienvenido);
        txtNombre = findViewById(R.id.textView_mostrar_nombre);
        txtApellido = findViewById(R.id.textView_mostrar_apellido);
        txtEmail = findViewById(R.id.textView_mostrar_email);
        txtTel = findViewById(R.id.textView_mostrar_telefono);
        progressBar = findViewById(R.id.progressBar);

        //Accion de click en la imagen para subir foto
        imageView = findViewById(R.id.imgView_perfil);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, UpdateProfileActivity.class);
                startActivity(intent);
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // Verificamos si el usuario ya esta logueado
        if (firebaseUser == null){
            Toast.makeText(UserProfileActivity.this, "Error al requerir usuario", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            checkEmailVerificado(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            mostrarPerfil(firebaseUser);
        }

    }

    @SuppressLint("ResourceAsColor")
    private void refreshSwipe() {

        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);
                swipeContainer.setRefreshing(false);
            }
        });
        //Configura los colores del refresh
        swipeContainer.setColorSchemeColors(R.color.aqua, R.color.verde_claro,
                R.color.naranja_claro, R.color.rojo);
    }

    private void checkEmailVerificado(FirebaseUser firebaseUser) {

        if (!firebaseUser.isEmailVerified()){
            mostrarAlerta();
        }
    }
    private void mostrarAlerta() {
        //Creamos una caja de dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
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

    private void mostrarPerfil(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //Obtener referencia de usuario de la DB
        DatabaseReference referencePerfil = FirebaseDatabase.getInstance().getReference("Usuario");
        referencePerfil.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readWriteUserDetails != null){
                    nombre = readWriteUserDetails.nombre;
                    apellido = readWriteUserDetails.apellido;
                    email = readWriteUserDetails.email;
                    tel = readWriteUserDetails.tel;

                    //obtenemos el grupo para gestionar los item del menu
                    grupo = readWriteUserDetails.grupo;

                    SharedPreferences sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userRole", grupo); // Guarda el userRole como String
                    editor.apply();
                    Log.e("Rol profile user", grupo);

                    txtVienvenido.setText("Bienvenido, "+nombre+"!");
                    txtNombre.setText(nombre);
                    txtApellido.setText(apellido);
                    txtEmail.setText(email);
                    txtTel.setText(tel);
                    //poner foto de usuario, despues del logueo
                    Uri uri = firebaseUser.getPhotoUrl();

                    //ImageView setImage uri Con Picasso
                    Picasso.with(UserProfileActivity.this).load(uri).into(imageView);

                } else {
                    Toast.makeText(UserProfileActivity.this, "Algo se ha cargado mal", Toast.LENGTH_SHORT).show();

                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Error al conectar a la DB", Toast.LENGTH_SHORT).show();
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

        if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(UserProfileActivity.this);
        } else if (id == R.id.menu_refresh){
            //Refresh Activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(UserProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(UserProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_mapa) {
            Intent intent = new Intent(UserProfileActivity.this, InicioActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UserProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent( UserProfileActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_logout) {
            firebaseAuth.signOut();
            Toast.makeText(UserProfileActivity.this, "Sesión Cerrrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
            //Limpiar form, por si el ususario preciona la tecla volver para ver los datos anteriores
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Cerramos UserProfileActivity
        } else {
            Toast.makeText(UserProfileActivity.this, "Ocurrió un error!!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}