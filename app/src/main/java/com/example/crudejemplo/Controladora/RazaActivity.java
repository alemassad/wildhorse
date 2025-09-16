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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.crudejemplo.Modelo.Raza;
import com.example.crudejemplo.R;
import com.example.crudejemplo.databinding.ActivityRazaBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RazaActivity extends DrawerBaseActivity {
    private EditText txtid, txtnombre, txtPelaje;
    private Button btnmod, btnreg, btneli, btnbuscaRaza, btnbuscar;
    private ListView lvDatos;

    ActivityRazaBinding activityBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = ActivityRazaBinding.inflate(getLayoutInflater());
        setContentView(activityBinding.getRoot());

        txtid = findViewById(R.id.txtid);
        txtnombre = findViewById(R.id.txtnom);
        txtPelaje = findViewById(R.id.txtPelaje);


        btnbuscar = findViewById(R.id.btnbus);
        btnbuscaRaza = findViewById(R.id.btnbusNom);
        btnmod = findViewById(R.id.btnmod);
        btnreg = findViewById(R.id.btnreg);
        btneli = findViewById(R.id.btneli);

        lvDatos = findViewById(R.id.lvDatos);

        botonBuscar();
        botonBuscarNombre();
        botonModificar();
        botonRegistrar();
        botonEliminar();
        listarRazas();

    }   //cierra el onCreate

    private void botonBuscar() {

        btnbuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtid.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(RazaActivity.this, "Escriba una Identificacion para BUSCAR!!!", Toast.LENGTH_SHORT).show();

                } else {
                    int id = Integer.parseInt(txtid.getText().toString());
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Raza.class.getSimpleName());
                    // DatabaseReference databaseReference = db.getReference().child("Dispositivo");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String auxId = Integer.toString(id);
                            boolean res = false;
                            for (DataSnapshot x : snapshot.getChildren()) {

                                if (auxId.equalsIgnoreCase(x.child("idRaza").getValue().toString())) {
                                    res = true;
                                    ocultarTeclado();
                                    txtnombre.setText(x.child("nombre").getValue().toString());
                                    txtPelaje.setText(x.child("pelaje").getValue().toString());

                                    // Registrar la acción en la auditoría usando AuditoriaUtil
                                    AuditoriaUtil.registrarAccion("Busca Razas por id");

                                    break;
                                }

                            }
                            if (!res) {
                                ocultarTeclado();
                                Toast.makeText(RazaActivity.this, "ID (" + auxId + ") no encontrado!!", Toast.LENGTH_SHORT).show();

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

        btnbuscaRaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtnombre.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(RazaActivity.this, "Escriba un Raza para BUSCAR!!!", Toast.LENGTH_SHORT).show();

                } else {

                    String raza = txtnombre.getText().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Raza.class.getSimpleName());
                    // DatabaseReference databaseReference = db.getReference().child("Dispositivo");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            boolean res = false;
                            for (DataSnapshot x : snapshot.getChildren()) {

                                if (raza.equalsIgnoreCase(x.child("nombre").getValue().toString())) {
                                    res = true;
                                    ocultarTeclado();
                                    txtid.setText(x.child("idRaza").getValue().toString());
                                    txtPelaje.setText(x.child("pelaje").getValue().toString());

                                    // Registrar la acción en la auditoría usando AuditoriaUtil
                                    AuditoriaUtil.registrarAccion("Busca Razas por nombre");

                                    break;
                                }

                            }
                            if (!res) {
                                ocultarTeclado();
                                Toast.makeText(RazaActivity.this, "Raza (" + raza + ") no encontrado!!", Toast.LENGTH_SHORT).show();

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
                        || txtnombre.getText().toString().trim().isEmpty()
                        || txtPelaje.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(RazaActivity.this, "Campos en blanco, completar para Modificar", Toast.LENGTH_SHORT).show();

                } else {
                    int id = Integer.parseInt(txtid.getText().toString());
                    String nombre = txtnombre.getText().toString();
                    String pelaje = txtPelaje.getText().toString();

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Raza.class.getSimpleName());

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String auxid = Integer.toString(id);
                            boolean res = false;
                            for (DataSnapshot tabla : snapshot.getChildren()) {

                                if (tabla.child("idRaza").getValue().toString().equals(auxid)) {
                                    res = true;
                                    ocultarTeclado();
                                    tabla.getRef().child("nombre").setValue(nombre);
                                    tabla.getRef().child("pelaje").setValue(pelaje);

                                    // Registrar la acción en la auditoría usando AuditoriaUtil
                                    AuditoriaUtil.registrarAccion("Modifica Razas");

                                    Toast.makeText(com.example.crudejemplo.Controladora.RazaActivity.this, "Registro modificado exitosamente!!", Toast.LENGTH_SHORT).show();
                                    txtid.setText("");
                                    txtnombre.setText("");
                                    txtPelaje.setText("");

                                    listarRazas();
                                    break;
                                }
                            }

                            if (!res) {

                                ocultarTeclado();
                                Toast.makeText(RazaActivity.this, "Identificacion (" + auxid + ") No encontrado.\nNo se puede modificar", Toast.LENGTH_SHORT).show();
                                txtid.setText("");
                                txtnombre.setText("");
                                txtPelaje.setText("");
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

                if (txtid.getText().toString().trim().isEmpty()
                        || txtnombre.getText().toString().trim().isEmpty()
                        || txtPelaje.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(RazaActivity.this, "Campos en blanco, completar!!", Toast.LENGTH_SHORT).show();

                } else {
                    int id = Integer.parseInt(txtid.getText().toString());
                    String nombre = txtnombre.getText().toString();
                    String pelaje = txtPelaje.getText().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Raza.class.getSimpleName());

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String auxid = Integer.toString(id);
                            boolean res = false;
                            for (DataSnapshot tabla : snapshot.getChildren()) {

                                if (tabla.child("idRaza").getValue().toString().equals(auxid)) {
                                    res = true;
                                    ocultarTeclado();
                                    Toast.makeText(RazaActivity.this, "Registro(" + auxid + ") ya existente", Toast.LENGTH_SHORT).show();
                                    break;
                                }

                            }
                            boolean res2 = false;
                            for (DataSnapshot x : snapshot.getChildren()) {

                                if (x.child("nombre").getValue().toString().equals(nombre)) {
                                    res2 = true;
                                    ocultarTeclado();
                                    Toast.makeText(RazaActivity.this, "El Nombre (" + nombre + ") ya existe", Toast.LENGTH_SHORT).show();
                                    break;
                                }

                            }
                            if (!res && !res2) {

                                Raza raza = new Raza(id, nombre, pelaje);
                                databaseReference.push().setValue(raza);

                                // Registrar la acción en la auditoría usando AuditoriaUtil
                                AuditoriaUtil.registrarAccion("Registra Razas");

                                ocultarTeclado();
                                Toast.makeText(RazaActivity.this, "Raza registrada correctamente", Toast.LENGTH_SHORT).show();
                                txtid.setText("");
                                txtnombre.setText("");
                                txtPelaje.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(RazaActivity.this, "Error al intentar registrar:" + error, Toast.LENGTH_SHORT).show();
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
                        txtnombre.getText().toString().trim().isEmpty()||
                        txtPelaje.getText().toString().trim().isEmpty()) {
                    ocultarTeclado();
                    Toast.makeText(RazaActivity.this, "Buscar una Identificacion o un Nombre para ELIMINAR!!!", Toast.LENGTH_SHORT).show();

                } else {
                    int id = Integer.parseInt(txtid.getText().toString());
                    String nombre = txtnombre.getText().toString();
                    String pelaje = txtPelaje.getText().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Raza.class.getSimpleName());
                    // DatabaseReference databaseReference = db.getReference().child("Dispositivo");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String auxId = Integer.toString(id);
                            final boolean[] res = {false};
                            for (DataSnapshot x : snapshot.getChildren()) {

                                if (auxId.equalsIgnoreCase(x.child("idRaza").getValue().toString())
                                        || nombre.equalsIgnoreCase(x.child("nombre").getValue().toString())) {

                                    AlertDialog.Builder a = new AlertDialog.Builder(RazaActivity.this);
                                    a.setCancelable(false);
                                    a.setTitle("ATENCION");
                                    a.setMessage("Estas por ELIMINAR el registro..");
                                    res[0] = true;
                                    Toast.makeText(com.example.crudejemplo.Controladora.RazaActivity.this, "ID ( "+auxId+" ) con ( "+nombre+" ) encontrado.\nUsted puede eliminar!!", Toast.LENGTH_SHORT).show();
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
                                            // Registrar la acción en la auditoría usando AuditoriaUtil
                                            AuditoriaUtil.registrarAccion("Elimina Razas");
                                            Toast.makeText(RazaActivity.this, "Registro eliminado exitosamente!!", Toast.LENGTH_SHORT).show();
                                            listarRazas();
                                            txtid.setText("");
                                            txtnombre.setText("");
                                            txtPelaje.setText("");
                                        }
                                    });
                                    a.show();
                                    break;
                                }
                            }
                            if (!res[0]) {
                                ocultarTeclado();
                                Toast.makeText(RazaActivity.this, "ID ( " + auxId + " ) con ( " + nombre + " ) no encontrado.\nNo se puede eliminar!!", Toast.LENGTH_SHORT).show();
                                txtid.setText("");
                                txtnombre.setText("");
                                txtPelaje.setText("");
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

    private void listarRazas() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference(Raza.class.getSimpleName());

        ArrayList<Raza> listGroup = new ArrayList<Raza>();
        ArrayAdapter<Raza> adapter = new ArrayAdapter<Raza>(RazaActivity.this, android.R.layout.simple_list_item_1, listGroup);
        lvDatos.setAdapter(adapter);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Raza raza = snapshot.getValue(Raza.class);
                listGroup.add(raza);
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
                Raza raza = listGroup.get(position);
                AlertDialog.Builder a = new AlertDialog.Builder(RazaActivity.this);
                a.setCancelable(true);
                a.setTitle("Raza Elegida");
                String msg = "ID : " + raza.getIdRaza() + "\n\n";
                msg += "Nombre : " + raza.getNombre()+ "\n\n";
                msg += "Pelaje : " + raza.getPelaje();

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
}
