package com.example.crudejemplo.Controladora;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.crudejemplo.Modelo.Grupo;
import com.example.crudejemplo.Modelo.Permiso;
import com.example.crudejemplo.R;
import com.example.crudejemplo.databinding.ActivityGrupoBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GrupoActivity extends DrawerBaseActivity {
    private EditText txtid, txtnombre, txtPermiso;
    private Button btnmod, btnreg, btneli, btnbuscaGrupo, btnbuscar;
    private ListView lvDatos;
    Spinner spinner;
    DatabaseReference databaseReference;

    public GrupoActivity() {

    }

    @SuppressLint("ResourceAsColor")

    ActivityGrupoBinding activityGrupoBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGrupoBinding = ActivityGrupoBinding.inflate(getLayoutInflater());
        setContentView(activityGrupoBinding.getRoot());

        txtid = findViewById(R.id.txtid);
        txtnombre = findViewById(R.id.txtnom);
        txtPermiso = findViewById(R.id.txtPermiso);
        spinner = findViewById(R.id.spinner);
        btnbuscar = findViewById(R.id.btnbus);
        btnbuscaGrupo = findViewById(R.id.btnbusNom);
        btnmod = findViewById(R.id.btnmod);
        btnreg = findViewById(R.id.btnreg);
        btneli = findViewById(R.id.btneli);
        lvDatos = findViewById(R.id.lvDatos);
        spinner = findViewById(R.id.spinner);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        txtPermiso.setEnabled(false);

        botonBuscar();
        botonBuscarNombre();
        botonModificar();
        botonRegistrar();
        botonEliminar();
        listarGrupos();
        //traemos los permisos
        LoadPermisos();
    }   //cierra el onCreate

    public void LoadPermisos() {
        List<Permiso> permisos = new ArrayList<>();

        databaseReference.child("Permiso").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        int id = Integer.parseInt(ds.child("id").getValue().toString());
                        String accion = ds.child("accion").getValue().toString();
                        String desripcion = ds.child("desc").getValue().toString();
                        //empesamos a leer la tabla permiso
                        ArrayAdapter<Permiso> arrayAdapter = new ArrayAdapter<>(GrupoActivity.this, android.R.layout.simple_dropdown_item_1line, permisos);
                        arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                        spinner.setAdapter(arrayAdapter);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String item = parent.getSelectedItem().toString();
                                txtPermiso.setText(item);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                        permisos.add(new Permiso(id, accion, desripcion));

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void botonBuscar() {

        btnbuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtid.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(GrupoActivity.this, "Escriba una Identificacion para BUSCAR!!!", Toast.LENGTH_SHORT).show();

                } else {
                    int id = Integer.parseInt(txtid.getText().toString());
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    //DatabaseReference databaseReference = db.getReference(Grupo.class.getSimpleName());
                    databaseReference = db.getReference().child("Grupo");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String auxId = Integer.toString(id);
                            boolean res = false;
                            for (DataSnapshot x : snapshot.getChildren()) {

                                if (auxId.equalsIgnoreCase(x.child("idGrupo").getValue().toString())) {
                                    res = true;
                                    ocultarTeclado();
                                    txtnombre.setText(x.child("nombreGrupo").getValue().toString());

                                    txtPermiso.setText(x.child("permiso").getValue().toString());
                                    LoadPermisos();
                                    // Registrar accion en la auditoría
                                    AuditoriaUtil.registrarAccion("Busca Grupo por ID");

                                    break;
                                }
                            }
                            if (!res) {
                                ocultarTeclado();
                                Toast.makeText(GrupoActivity.this, "ID (" + auxId + ") no encontrado!!", Toast.LENGTH_SHORT).show();

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

    private void botonBuscarNombre() {

        btnbuscaGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtnombre.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(GrupoActivity.this, "Escriba un Grupo para BUSCAR!!!", Toast.LENGTH_SHORT).show();

                } else {

                    String grupo = txtnombre.getText().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Grupo.class.getSimpleName());
                    // DatabaseReference databaseReference = db.getReference().child("Dispositivo");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            boolean res = false;
                            for (DataSnapshot x : snapshot.getChildren()) {

                                if (grupo.equalsIgnoreCase(x.child("nombreGrupo").getValue().toString())) {
                                    res = true;
                                    ocultarTeclado();
                                    txtid.setText(x.child("idGrupo").getValue().toString());
                                    txtPermiso.setText(x.child("permiso").getValue().toString());
                                    LoadPermisos();

                                    // Registrar accion en la auditoría
                                    AuditoriaUtil.registrarAccion("Busca Grupo por Nombre");

                                    break;
                                }

                            }
                            if (!res) {
                                ocultarTeclado();
                                Toast.makeText(GrupoActivity.this, "Grupo (" + grupo + ") no encontrado!!", Toast.LENGTH_SHORT).show();

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

    private void botonModificar() {

        btnmod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtid.getText().toString().trim().isEmpty()
                        || txtnombre.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(GrupoActivity.this, "Campos en blanco, completar para Modificar", Toast.LENGTH_SHORT).show();

                } else {
                    int id = Integer.parseInt(txtid.getText().toString());
                    String nombre = txtnombre.getText().toString();
                    String permiso = txtPermiso.getText().toString();

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Grupo.class.getSimpleName());

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String auxid = Integer.toString(id);
                            boolean res = false;
                            for (DataSnapshot tabla : snapshot.getChildren()) {

                                if (tabla.child("idGrupo").getValue().toString().equals(auxid)) {
                                    res = true;
                                    ocultarTeclado();
                                    tabla.getRef().child("nombreGrupo").setValue(nombre);
                                    LoadPermisos();
                                    tabla.getRef().child("permiso").setValue(permiso);

                                    // Registrar accion en la auditoría
                                    AuditoriaUtil.registrarAccion("Modifica Grupo");

                                    Toast.makeText(com.example.crudejemplo.Controladora.GrupoActivity.this, "Registro modificado exitosamente!!", Toast.LENGTH_SHORT).show();
                                    txtid.setText("");
                                    txtnombre.setText("");
                                    listarGrupos();
                                    break;
                                }
                            }

                            if (!res) {

                                ocultarTeclado();
                                Toast.makeText(GrupoActivity.this, "Identificacion (" + auxid + ") No encontrado.\nNo se puede modificar", Toast.LENGTH_SHORT).show();
                                txtid.setText("");
                                txtnombre.setText("");
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

    private void botonRegistrar() {
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtid.getText().toString().trim().isEmpty() ||
                        txtPermiso.getText().toString().trim().isEmpty()
                        || txtnombre.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(GrupoActivity.this, "Campos en blanco, completar!!", Toast.LENGTH_SHORT).show();

                } else {
                    String[] desc = new String[1];
                    int id = Integer.parseInt(txtid.getText().toString());
                    String nombre = txtnombre.getText().toString();
                    String permiso = txtPermiso.getText().toString();

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Grupo.class.getSimpleName());

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String auxid = Integer.toString(id);
                            boolean res = false;
                            boolean res2 = false;
                            for (DataSnapshot tabla : snapshot.getChildren()) {

                                if (tabla.child("idGrupo").getValue().toString().equals(auxid)) {
                                    res = true;
                                    ocultarTeclado();
                                    Toast.makeText(GrupoActivity.this, "Registro(" + auxid + ") ya existente", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }

                            for (DataSnapshot x : snapshot.getChildren()) {

                                if (x.child("nombreGrupo").getValue().toString().equals(nombre)) {
                                    res2 = true;
                                    ocultarTeclado();
                                    Toast.makeText(GrupoActivity.this, "El Nombre (" + nombre + ") ya existe", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }

                            if (!res && !res2) {

                                Grupo grupo = new Grupo(id, nombre, permiso);
                                databaseReference.push().setValue(grupo);
                                ocultarTeclado();

                                // Registrar accion en la auditoría
                                AuditoriaUtil.registrarAccion("Registra Grupo");

                                Toast.makeText(GrupoActivity.this, "Grupo registrado correctamente", Toast.LENGTH_SHORT).show();
                                txtid.setText("");
                                txtnombre.setText("");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(GrupoActivity.this, "Error al intentar registrar:" + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }//Cierra el if else
            }
        });

    }//Cierra el metodo boton registrar

    private void botonEliminar() {
        btneli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtid.getText().toString().trim().isEmpty() ||
                        txtPermiso.getText().toString().trim().isEmpty() ||
                        txtnombre.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(GrupoActivity.this, "Buscar una Identificacion o una Nombre para ELIMINAR!!!", Toast.LENGTH_SHORT).show();

                } else {
                    int id = Integer.parseInt(txtid.getText().toString());
                    String nombre = txtnombre.getText().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Grupo.class.getSimpleName());
                    // DatabaseReference databaseReference = db.getReference().child("Dispositivo");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String auxId = Integer.toString(id);
                            final boolean[] res = {false};
                            for (DataSnapshot x : snapshot.getChildren()) {

                                if (auxId.equalsIgnoreCase(x.child("idGrupo").getValue().toString())
                                        || nombre.equalsIgnoreCase(x.child("nombreGrupo").getValue().toString())) {

                                    AlertDialog.Builder a = new AlertDialog.Builder(GrupoActivity.this);
                                    a.setCancelable(false);
                                    a.setTitle("ATENCION");
                                    a.setMessage("Estas por ELIMINAR el registro..");
                                    res[0] = true;
                                    Toast.makeText(GrupoActivity.this, "ID ( " + auxId + " ) con ( " + nombre + " ) encontrado.\nUsted puede eliminar!!", Toast.LENGTH_SHORT).show();
                                    a.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    a.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ocultarTeclado();
                                            x.getRef().removeValue();
                                            Toast.makeText(GrupoActivity.this, "Registro eliminado exitosamente!!", Toast.LENGTH_SHORT).show();
                                            // Registrar accion en la auditoría
                                            AuditoriaUtil.registrarAccion("Elimina Grupo");

                                            listarGrupos();
                                            txtid.setText("");
                                            txtnombre.setText("");
                                            txtPermiso.setText("");
                                        }
                                    });
                                    a.show();
                                    break;
                                }
                            }
                            if (!res[0]) {
                                ocultarTeclado();
                                Toast.makeText(GrupoActivity.this, "ID ( " + auxId + " ) con ( " + nombre + " ) no encontrado.\nNo se puede eliminar!!", Toast.LENGTH_SHORT).show();
                                txtid.setText("");
                                txtnombre.setText("");
                                txtPermiso.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } //cierra el if/else

            }
        });
    }//cierra el metodo boton Eliminar

    private void listarGrupos() {
        final int[] conteoGrupo = {0};
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference(Grupo.class.getSimpleName());

        ArrayList<Grupo> listGroup = new ArrayList<Grupo>();

        ArrayAdapter<Grupo> adaptador = new ArrayAdapter<Grupo>(GrupoActivity.this, android.R.layout.simple_list_item_1, listGroup);
        lvDatos.setAdapter(adaptador);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Grupo grupo = snapshot.getValue(Grupo.class);
                listGroup.add(grupo);
                adaptador.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                listGroup.clear();
                adaptador.notifyDataSetChanged();
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
                if (position < listGroup.size()) {
                    Grupo grupo = listGroup.get(position);
                    AlertDialog.Builder a = new AlertDialog.Builder(GrupoActivity.this);
                    a.setCancelable(true);
                    a.setTitle("Grupo Elegido");
                    String msg = "ID: " + grupo.getIdGrupo() + "\n\n";
                    msg += "Nombre: " + grupo.getNombreGrupo() + "\n\n";
                    msg += "Permiso: " + grupo.getPermiso() + "\n\n";

                    a.setMessage(msg);
                    a.show();
                }/*else{
                    Toast.makeText(GrupoActivity.this, "Este es el último elemento, no se puede mostrar", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

    }//cierra el metodo listar grupo

    private void ocultarTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    } // Cierra el método ocultarTeclado.
}