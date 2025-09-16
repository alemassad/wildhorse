package com.example.crudejemplo.Controladora;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;

import androidx.media3.common.util.UnstableApi;

import com.example.crudejemplo.Modelo.Grupo;
import com.example.crudejemplo.Modelo.Usuario;
import com.example.crudejemplo.R;
import com.example.crudejemplo.databinding.ActivityUsuarioBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.List;

public class UsuarioActivity extends DrawerBaseActivity {
    private EditText txtdni, txtnom, txtape, txtemail, txttel, txtGrupo;
    private Button btnbus, btnmod, btnreg, btneli, btnbusNom, btnbusApe;
    private ListView lvDatos;
    private int index;
    Spinner spinner;

    ActivityUsuarioBinding activityBinding;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = ActivityUsuarioBinding.inflate(getLayoutInflater());
        setContentView(activityBinding.getRoot());

        txtdni = findViewById(R.id.txtid);
        txtnom = findViewById(R.id.txtnom);
        txtape = findViewById(R.id.txtape);
        txttel = findViewById(R.id.txttel);
        txtemail = findViewById(R.id.txtemail);
        txtGrupo = findViewById(R.id.txtGrupo);
        btnbus = findViewById(R.id.btnbus);
        btnmod = findViewById(R.id.btnmod);
        btnreg = findViewById(R.id.btnreg);
        btneli = findViewById(R.id.btneli);
        lvDatos = findViewById(R.id.lvDatos);
        btnbusNom = findViewById(R.id.btnbusNom);
        btnbusApe = findViewById(R.id.btnbusApe);
        spinner = findViewById(R.id.spinner);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        txtGrupo.setEnabled(false);
        txtemail.setEnabled(false);

        botonBuscar();
        botonBuscarNom();
        botonBuscarApe();
        botonModificar();
        botonEliminar();
        listarUsuarios();
        LoadGrupos();

    }   //cierra el onCreate

    @OptIn(markerClass = UnstableApi.class)
    public void LoadGrupos() {
        List<Grupo> grupos = new ArrayList<>();
        Log.e("LoadGrupos", "grupo: " + grupos);

        databaseReference.child("Grupo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        // Obtener los valores de los campos
                        String nombreGrupo = ds.child("nombreGrupo").getValue(String.class);
                        String permiso = ds.child("permiso").getValue(String.class);

                        // Agregar el grupo a la lista
                        grupos.add(new Grupo(nombreGrupo, permiso));
                    }

                    // Crear el adaptador para el Spinner
                    ArrayAdapter<Grupo> adapter = new ArrayAdapter<>(UsuarioActivity.this, android.R.layout.simple_dropdown_item_1line, grupos); // Lista de objetos Grupo
                    adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice); // Layout para la vista desplegable
                    spinner.setAdapter(adapter);

                    // Establecer la selección del Spinner según el índice
                    spinner.setSelection(index);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Grupo grupoSeleccionado = (Grupo) parent.getSelectedItem();

                            // Solo actualizar el texto de txtGrupo si no es el valor por defecto (índice 3)
                            if (grupoSeleccionado != null) {
                                txtGrupo.setText(grupoSeleccionado.getNombreGrupo()); // Mostrar el nombre del grupo en txtGrupo
                            } else {
                                txtGrupo.setText(""); // Limpiar el texto si el índice es 3
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                }
            }
            @OptIn(markerClass = UnstableApi.class)
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores de la conexión a Firebase
                Log.e("LoadGrupos", "Error al cargar grupos: " + error.getMessage());
            }
        });

    }

    private void botonBuscar() {

        btnbus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (txtdni.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(UsuarioActivity.this, "Escriba una Identificacion para BUSCAR!!!", Toast.LENGTH_SHORT).show();

                } else {

                    String id = txtdni.getText().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference().child("Usuario");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @OptIn(markerClass = UnstableApi.class)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            boolean res = false;
                            for (DataSnapshot x : snapshot.getChildren()) {

                                if (id.equalsIgnoreCase(x.child("dni").getValue().toString())) {
                                    res = true;
                                    ocultarTeclado();
                                    txtnom.setText(x.child("nombre").getValue().toString());
                                    txtape.setText(x.child("apellido").getValue().toString());
                                    txtemail.setText(x.child("email").getValue().toString());
                                    txttel.setText(x.child("tel").getValue().toString());
                                    txtGrupo.setText(x.child("grupo").getValue().toString());

                                    // Registrar acción al buscar un usuario
                                    AuditoriaUtil.registrarAccion("Buscar usuario por DNI");

                                    break;
                                }
                            }
                            if (!res) {
                                ocultarTeclado();
                                Toast.makeText(UsuarioActivity.this, "DNI (" + id + ") no encontrado!!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } //cierra el if/else
            }
        });

    }//cierra el metodo boton buscar x ID

    private void botonBuscarNom() {

        btnbusNom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtnom.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(UsuarioActivity.this, "Escriba un Nombre para BUSCAR!!!", Toast.LENGTH_SHORT).show();

                } else {

                    String nom = txtnom.getText().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Usuario.class.getSimpleName());
                    // DatabaseReference databaseReference = db.getReference().child("Usuario");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            boolean res = false;
                            for (DataSnapshot x : snapshot.getChildren()) {

                                if (nom.equalsIgnoreCase(x.child("nombre").getValue().toString())) {
                                    res = true;
                                    ocultarTeclado();
                                    txtdni.setText(x.child("dni").getValue().toString());
                                    txtape.setText(x.child("apellido").getValue().toString());
                                    txtemail.setText(x.child("email").getValue().toString());
                                    txttel.setText(x.child("tel").getValue().toString());
                                    txtGrupo.setText(x.child("grupo").getValue().toString());
                                    String grupo = x.child("grupo").getValue().toString();
                                    // Registrar acción al buscar un usuario
                                    AuditoriaUtil.registrarAccion("Buscar usuario por Nombre");
                                    //LoadGrupos();
                                    break;
                                }
                            }
                            if (!res) {
                                ocultarTeclado();
                                Toast.makeText(UsuarioActivity.this, "NOMBRE (" + nom + ") no encontrado!!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } //cierra el if/else
            }
        });

    }//cierra el metodo boton buscar x Nombre

    private void botonBuscarApe() {

        btnbusApe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtape.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(UsuarioActivity.this, "Escriba un Apellido para BUSCAR!!!", Toast.LENGTH_SHORT).show();

                } else {

                    String ape = txtape.getText().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Usuario.class.getSimpleName());
                    // DatabaseReference databaseReference = db.getReference().child("Usuario");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            boolean res = false;
                            for (DataSnapshot x : snapshot.getChildren()) {

                                if (ape.equalsIgnoreCase(x.child("apellido").getValue().toString())) {
                                    res = true;
                                    ocultarTeclado();
                                    txtdni.setText(x.child("dni").getValue().toString());
                                    txtnom.setText(x.child("nombre").getValue().toString());
                                    txtemail.setText(x.child("email").getValue().toString());
                                    txttel.setText(x.child("tel").getValue().toString());
                                    txtGrupo.setText(x.child("grupo").getValue().toString());
                                    String grupo = x.child("grupo").getValue().toString();
                                    // Registrar acción al buscar un usuario
                                    AuditoriaUtil.registrarAccion("Buscar usuario por Apellido");
                                    //LoadGrupos();
                                    break;
                                }

                            }
                            if (!res) {
                                ocultarTeclado();
                                Toast.makeText(UsuarioActivity.this, "APELLIDO (" + ape + ") no encontrado!!", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } //cierra el if/else
            }
        });

    }//cierra el metodo boton buscar x Apellido

    private void botonModificar() {

        btnmod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtemail.getText().toString().trim().isEmpty()
                        || txtdni.getText().toString().trim().isEmpty()
                        || txtnom.getText().toString().trim().isEmpty()
                        || txtape.getText().toString().trim().isEmpty()
                        || txttel.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(UsuarioActivity.this, "Campos en blanco, completar para Modificar", Toast.LENGTH_SHORT).show();

                } else {
                    String email = txtemail.getText().toString();
                    String dni = txtdni.getText().toString();
                    String nom = txtnom.getText().toString();
                    String ape = txtape.getText().toString();
                    String tel = txttel.getText().toString();
                    String grupo = txtGrupo.getText().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Usuario.class.getSimpleName());

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String auxid = email;
                            boolean res = false;
                            for (DataSnapshot tabla : snapshot.getChildren()) {

                                if (tabla.child("email").getValue().toString().equals(auxid)) {
                                    res = true;
                                    ocultarTeclado();
                                    tabla.getRef().child("dni").setValue(dni);
                                    tabla.getRef().child("nombre").setValue(nom);
                                    tabla.getRef().child("apellido").setValue(ape);
                                    tabla.getRef().child("tel").setValue(tel);
                                    LoadGrupos();
                                    tabla.getRef().child("grupo").setValue(grupo);

                                    // Registrar la acción en la auditoría
                                    AuditoriaUtil.registrarAccion("Modificar usuario");

                                    Toast.makeText(com.example.crudejemplo.Controladora.UsuarioActivity.this, "Registro modificado exitosamente!!", Toast.LENGTH_SHORT).show();
                                    txtemail.setText("");
                                    txtdni.setText("");
                                    txtnom.setText("");
                                    txtape.setText("");
                                    txttel.setText("");
                                    txtGrupo.setText("");
                                    listarUsuarios();
                                    break;
                                }
                            }
                            if (!res) {
                                ocultarTeclado();
                                Toast.makeText(UsuarioActivity.this, "Identificacion (" + auxid + ") No encontrado.\nNo se puede modificar", Toast.LENGTH_SHORT).show();
                                txtemail.setText("");
                                txtdni.setText("");
                                txtnom.setText("");
                                txtape.setText("");
                                txttel.setText("");
                                txtGrupo.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }//Cierra el if else

            }
        });

    } //cierra el metodo boton modificar

    private void botonEliminar() {
        btneli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtdni.getText().toString().trim().isEmpty() ||
                        txtnom.getText().toString().trim().isEmpty() || txtape.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(UsuarioActivity.this, "Buscar una Identificacion, un nombre o un apellido para ELIMINAR!!!", Toast.LENGTH_SHORT).show();

                } else {
                    String id = txtemail.getText().toString();
                    String dni = txtdni.getText().toString();
                    String nombre = txtnom.getText().toString();
                    String apellido = txtape.getText().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Usuario.class.getSimpleName());
                    //DatabaseReference databaseReference = db.getReference().child("Usuario");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String auxId = id;
                            final boolean[] res = {false};
                            for (DataSnapshot x : snapshot.getChildren()) {

                                if (auxId.equalsIgnoreCase(x.child("email").getValue().toString())
                                        || dni.equalsIgnoreCase(x.child("dni").getValue().toString())
                                        || nombre.equalsIgnoreCase(x.child("nombre").getValue().toString())
                                        || apellido.equalsIgnoreCase(x.child("apellido").getValue().toString())) {

                                    AlertDialog.Builder a = new AlertDialog.Builder(UsuarioActivity.this);
                                    a.setCancelable(false);
                                    a.setTitle("ATENCION");
                                    a.setMessage("Estas por ELIMINAR el registro..");
                                    res[0] = true;
                                    Toast.makeText(UsuarioActivity.this, "( " + auxId + " ) encontrado.\nUsted puede eliminar!!", Toast.LENGTH_SHORT).show();
                                    a.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    a.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            String email = x.child("email").getValue().toString();
                                            String password = x.child("pass").getValue().toString();
                                            ocultarTeclado();
                                            // Registrar acción al eliminar un usuario
                                            AuditoriaUtil.registrarAccion("Eliminar usuario");

                                            x.getRef().removeValue();
                                            listarUsuarios();
                                            // Llama a la función para eliminar el perfil autenticado
                                            deleteUser(email, password);
                                            txtemail.setText("");
                                            txtdni.setText("");
                                            txtnom.setText("");
                                            txtape.setText("");
                                            txttel.setText("");
                                            txtGrupo.setText("");
                                        }
                                    });
                                    a.show();
                                    break;
                                }
                            }
                            if (!res[0]) {
                                ocultarTeclado();
                                Toast.makeText(UsuarioActivity.this, "( " + auxId + " ) no encontrado.\nNo se puede eliminar!!", Toast.LENGTH_SHORT).show();
                                txtemail.setText("");
                                txtdni.setText("");
                                txtnom.setText("");
                                txtape.setText("");
                                txttel.setText("");
                                txtGrupo.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } //cierra el if/else

            }

            //Eliminamos los datos del usuario en AuthFirebase y su foto de perfil
            public void deleteUser(String email, String password) {
                FirebaseAuth auth = FirebaseAuth.getInstance();

                // Autentica al usuario con email y contraseña
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Autenticación exitosa, obtén el usuario actual

                                FirebaseUser user = auth.getCurrentUser();
                                // Elimina al usuario
                                user.delete()
                                        .addOnCompleteListener(deleteTask -> {
                                            if (deleteTask.isSuccessful()) {
                                                // Usuario eliminado exitosamente
                                                Toast.makeText(UsuarioActivity.this, "Usuario ( " + email + " )  eliminado exitosamente", Toast.LENGTH_SHORT).show();

                                                //Ahora borramos foto de perfil
                                                if (user.getPhotoUrl() != null){ // vemos si hay fotos cargadas antes de borrar
                                                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                                    StorageReference storageReference = firebaseStorage.getReferenceFromUrl(user.getPhotoUrl().toString());
                                                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.d("UsuarioActivity", "OnSuccess: Photo Deleted");
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("UsuarioActivity", e.getMessage());
                                                            Toast.makeText(UsuarioActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            } else {
                                                // Error al eliminar al usuario
                                                Toast.makeText(UsuarioActivity.this, "Error al eliminar al usuario: "+deleteTask.getException(), Toast.LENGTH_SHORT).show();
                                                Log.w("TAG", "Error al eliminar al usuario", deleteTask.getException());
                                            }
                                        });
                            } else {
                                // Error de autenticación
                                Toast.makeText(UsuarioActivity.this, "Error de autenticar al usuario( " + email +"  ): "+task.getException(), Toast.LENGTH_SHORT).show();
                                Log.w("TAG", "Error de autenticación", task.getException());
                            }
                        });
            }

        });
    }//cierra el metodo boton Eliminar

    private void listarUsuarios() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference().child("Usuario");
        ArrayList<Usuario> listaUsuario = new ArrayList<Usuario>();
        ArrayAdapter<Usuario> adapter = new ArrayAdapter<Usuario>(UsuarioActivity.this, android.R.layout.simple_list_item_1, listaUsuario);
        lvDatos.setAdapter(adapter);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                listaUsuario.add(usuario);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        lvDatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Usuario usuario = listaUsuario.get(position);
                AlertDialog.Builder a = new AlertDialog.Builder(UsuarioActivity.this);
                a.setCancelable(true);

                a.setTitle("Usuario Elegido");
                String msg = "Email: " + usuario.getEmail() + "\n\n";
                msg += "Dni: " + usuario.getDni() + "\n\n";
                msg += "Nombre: " + usuario.getNombre() + "\n\n";
                msg += "Apellido: " + usuario.getApellido() + "\n\n";
                msg += "Grupo: " + usuario.getGrupo() + "\n\n";
                a.setMessage(msg);
                a.show();
            }
        });

    }//cierra el metodo listarUsuarios

    private void ocultarTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    } // Cierra el método ocultarTeclado.


}   //cierra la clase