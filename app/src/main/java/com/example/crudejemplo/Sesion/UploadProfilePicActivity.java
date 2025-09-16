package com.example.crudejemplo.Sesion;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crudejemplo.R;
import com.example.crudejemplo.Controladora.InicioActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadProfilePicActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private ImageView imageViewUpload;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private static final int PICK_IMAGE_REQUEST = 1;
    public Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_profile_pic);

        getSupportActionBar().setTitle("Actualizar foto perfil");

        firebaseAuth = FirebaseAuth.getInstance();

        Button buttonChoseFoto = findViewById(R.id.btn_elige_foto);
        Button buttonUploadFoto = findViewById(R.id.btn_subir_foto);
        progressBar = findViewById(R.id.progressBar);
        imageViewUpload = findViewById(R.id.imagen_perfil);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("MostrarFotos");

        Uri uri = firebaseUser.getPhotoUrl();

        //Poner la foto del usuario. Desde Picasso al imageView
        Picasso.with(UploadProfilePicActivity.this).load(uri).into(imageViewUpload);
        buttonChoseFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirFileElegida();
            }
        });
        //Subir imagen
        buttonUploadFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                SubirFoto();
            }
        });

    }
    private void abrirFileElegida(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!= null && data.getData() != null){
            uriImage = data.getData();
            imageViewUpload.setImageURI(uriImage);
        }
    }
    private void SubirFoto(){
        if (uriImage != null){
            StorageReference reference = storageReference.child(firebaseAuth.getCurrentUser().getUid()
                    + "/MostrarFotos" + getFileExtension(uriImage));

            //Subir imagen a la DB
            reference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            firebaseUser = firebaseAuth.getCurrentUser();

                            UserProfileChangeRequest profileupdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileupdates);
                        }
                    });
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UploadProfilePicActivity.this, "Carga Exitosa", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UploadProfilePicActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadProfilePicActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(UploadProfilePicActivity.this, "Archivo no seleccionado", Toast.LENGTH_SHORT).show();
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
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
            Intent intent = new Intent(UploadProfilePicActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(UploadProfilePicActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_mapa) {
            Intent intent = new Intent(UploadProfilePicActivity.this, InicioActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UploadProfilePicActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent( UploadProfilePicActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_logout) {
            firebaseAuth.signOut();
            Toast.makeText(UploadProfilePicActivity.this, "Sesión Cerrrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UploadProfilePicActivity.this, InicioActivity.class);
            //Limpiar form, por si el ususario preciona la tecla volver para ver los datos anteriores
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Cerramos UserProfileActivity
        } else {
            Toast.makeText(
                    UploadProfilePicActivity.this, "Ocurrió un error!!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}