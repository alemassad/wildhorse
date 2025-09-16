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

import com.example.crudejemplo.Modelo.Dispositivo;
import com.example.crudejemplo.R;
import com.example.crudejemplo.databinding.ActivityDispositivoBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DispositivoActivity extends DrawerBaseActivity {
    ActivityDispositivoBinding activityDispositivoBinding;
    private EditText txtid, txtmarca, txtdesc, txtestado;
    private Button btnmod, btnreg, btneli, btnbusDispo, btnbusMarca;
    private ListView lvDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDispositivoBinding = ActivityDispositivoBinding.inflate(getLayoutInflater());
        setContentView(activityDispositivoBinding.getRoot());

        txtid   = findViewById(R.id.txtidDispo);
        txtmarca  = findViewById(R.id.txtmarca);
        txtdesc   = findViewById(R.id.txtdesc);
        txtestado   = findViewById(R.id.txtestado);

        btnbusDispo = findViewById(R.id.btnbusDispo);
        btnbusMarca = findViewById(R.id.btnbusMarca);
        btnmod  = findViewById(R.id.btnmod);
        btnreg  = findViewById(R.id.btnreg);
        btneli  = findViewById(R.id.btneli);

        lvDatos = findViewById(R.id.lvDatos);

        botonBuscarDispo();
        botonBuscarMarca();
        botonModificar();
        botonRegistrar();
        botonEliminar();
        listarDispositivos();

    }   //cierra el onCreate
    private void botonBuscarDispo(){

        btnbusDispo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtid.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(DispositivoActivity.this, "Escriba una Identificacion para BUSCAR!!!", Toast.LENGTH_SHORT).show();

                } else {
                    int id = Integer.parseInt(txtid.getText().toString());
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Dispositivo.class.getSimpleName());
                    // DatabaseReference databaseReference = db.getReference().child("Dispositivo");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String auxId = Integer.toString(id);
                            boolean res = false;
                            for (DataSnapshot x : snapshot.getChildren()) {

                                if (auxId.equalsIgnoreCase(x.child("idDispositivo").getValue().toString())) {
                                    res = true;
                                    ocultarTeclado();
                                    txtmarca.setText(x.child("marca").getValue().toString());
                                    txtdesc.setText(x.child("descripcion").getValue().toString());
                                    txtestado.setText(x.child("estado").getValue().toString());

                                    // Registrar accion en la auditoría
                                    AuditoriaUtil.registrarAccion("Busca Dispositivo por ID");
                                    break;
                                }

                            }
                            if (!res){
                                ocultarTeclado();
                                Toast.makeText(DispositivoActivity.this, "ID ("+auxId+") no encontrado!!", Toast.LENGTH_SHORT).show();

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
    private void botonBuscarMarca(){

        btnbusMarca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtmarca.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(DispositivoActivity.this, "Escriba una Marca para BUSCAR!!!", Toast.LENGTH_SHORT).show();

                } else {

                    String marca = txtmarca.getText().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Dispositivo.class.getSimpleName());
                    // DatabaseReference databaseReference = db.getReference().child("Dispositivo");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            boolean res = false;
                            for (DataSnapshot x : snapshot.getChildren()) {

                                if (marca.equalsIgnoreCase(x.child("marca").getValue().toString())) {
                                    res = true;
                                    ocultarTeclado();
                                    txtid.setText(x.child("idDispositivo").getValue().toString());
                                    txtdesc.setText(x.child("descripcion").getValue().toString());
                                    txtestado.setText(x.child("estado").getValue().toString());

                                    // Registrar accion en la auditoría
                                    AuditoriaUtil.registrarAccion("Busca Dispositivo por Marca");

                                    break;
                                }

                            }
                            if (!res){
                                ocultarTeclado();
                                Toast.makeText(DispositivoActivity.this, "MARCA ("+marca+") no encontrada!!", Toast.LENGTH_SHORT).show();

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
     private void botonModificar(){

        btnmod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtid.getText().toString().trim().isEmpty()
                        || txtmarca.getText().toString().trim().isEmpty()
                        || txtdesc.getText().toString().trim().isEmpty()
                        || txtestado.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(DispositivoActivity.this, "Campos en blanco, completar para Modificar", Toast.LENGTH_SHORT).show();

                }else{
                    int id = Integer.parseInt(txtid.getText().toString());
                    String marca = txtmarca.getText().toString();
                    String desc = txtdesc.getText().toString();
                    String estado = txtestado.getText().toString();

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Dispositivo.class.getSimpleName());

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String auxid= Integer.toString(id);
                            boolean res = false;
                            for (DataSnapshot tabla: snapshot.getChildren()){

                                if (tabla.child("idDispositivo").getValue().toString().equals(auxid)){
                                    res = true;
                                    ocultarTeclado();
                                    tabla.getRef().child("marca").setValue(marca);
                                    tabla.getRef().child("descripcion").setValue(desc);
                                    tabla.getRef().child("estado").setValue(estado);
                                    Toast.makeText(com.example.crudejemplo.Controladora.DispositivoActivity.this, "Registro modificado exitosamente!!", Toast.LENGTH_SHORT).show();

                                    // Registrar accion en la auditoría
                                    AuditoriaUtil.registrarAccion("Modifica Dispositivo");

                                    txtid.setText("");
                                    txtmarca.setText("");
                                    txtdesc.setText("");
                                    txtestado.setText("");

                                    listarDispositivos();
                                    break;
                                }
                            }

                            if(!res){

                                ocultarTeclado();
                                Toast.makeText(DispositivoActivity.this, "Identificacion ("+auxid+") No encontrado.\nNo se puede modificar", Toast.LENGTH_SHORT).show();
                                txtid.setText("");
                                txtmarca.setText("");
                                txtdesc.setText("");
                                txtestado.setText("");
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
    private void botonRegistrar(){
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtid.getText().toString().trim().isEmpty()
                        || txtmarca.getText().toString().trim().isEmpty()
                        || txtdesc.getText().toString().trim().isEmpty()
                        || txtestado.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(DispositivoActivity.this, "Campos en blanco, completar!!", Toast.LENGTH_SHORT).show();

                }else{
                    int id = Integer.parseInt(txtid.getText().toString());
                    String marca = txtmarca.getText().toString();
                    String desc = txtdesc.getText().toString();
                    String estado = txtestado.getText().toString();

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Dispositivo.class.getSimpleName());

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String auxid= Integer.toString(id);
                            boolean res = false;
                            for (DataSnapshot tabla: snapshot.getChildren()){

                                if (tabla.child("idDispositivo").getValue().toString().equals(auxid)){
                                    res = true;
                                    ocultarTeclado();
                                    Toast.makeText(DispositivoActivity.this, "Registro("+auxid+") ya existente", Toast.LENGTH_SHORT).show();
                                    break;
                                }

                            }
                            boolean res2 = false;
                            for (DataSnapshot x : snapshot.getChildren()){

                                if (x.child("marca").getValue().toString().equals(marca)){
                                    res2 = true;
                                    ocultarTeclado();
                                    Toast.makeText(DispositivoActivity.this, "La MARCA ("+marca+") ya existe", Toast.LENGTH_SHORT).show();
                                    break;
                                }

                            }
                            if(!res && !res2){

                                Dispositivo dispositivo = new Dispositivo(id, marca, desc, estado);
                                databaseReference.push().setValue(dispositivo);
                                ocultarTeclado();

                                // Registrar accion en la auditoría
                                AuditoriaUtil.registrarAccion("Registra Dispositivo");

                                Toast.makeText(DispositivoActivity.this, "Dispositivo registrado correctamente", Toast.LENGTH_SHORT).show();
                                txtid.setText("");
                                txtmarca.setText("");
                                txtdesc.setText("");
                                txtestado.setText("");

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(DispositivoActivity.this, "Error al intentar registrar:" + error , Toast.LENGTH_SHORT).show();
                        }
                    });

                }//Cierra el if else

            }
        });

    }//Cierra el metodo boton registrar
    private void botonEliminar(){
        btneli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtid.getText().toString().trim().isEmpty() ||
                        txtmarca.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(DispositivoActivity.this, "Buscar una Identificacion o una Marca para ELIMINAR!!!", Toast.LENGTH_SHORT).show();

                } else {
                    int id = Integer.parseInt(txtid.getText().toString());
                    String marca = txtmarca.getText().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Dispositivo.class.getSimpleName());
                    // DatabaseReference databaseReference = db.getReference().child("Dispositivo");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String auxId = Integer.toString(id);
                            final boolean[] res = {false};
                            for (DataSnapshot x : snapshot.getChildren()) {

                                if (auxId.equalsIgnoreCase(x.child("idDispositivo").getValue().toString())
                                        || marca.equalsIgnoreCase(x.child("marca").getValue().toString())) {

                                    AlertDialog.Builder a = new AlertDialog.Builder(DispositivoActivity.this);
                                    a.setCancelable(false);
                                    a.setTitle("ATENCION");
                                    a.setMessage("Estas por ELIMINAR el registro..");
                                    res[0] = true;
                                    Toast.makeText(com.example.crudejemplo.Controladora.DispositivoActivity.this, "ID ( "+auxId+" ) con ( "+marca+" ) encontrado.\nUsted puede eliminar!!", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(DispositivoActivity.this, "Registro eliminado exitosamente!!", Toast.LENGTH_SHORT).show();

                                            // Registrar accion en la auditoría
                                            AuditoriaUtil.registrarAccion("Elimina Dispositivo");

                                            listarDispositivos();
                                            txtid.setText("");
                                            txtmarca.setText("");
                                            txtdesc.setText("");
                                            txtestado.setText("");
                                        }
                                    });
                                    a.show();
                                    break;
                                }
                            }
                            if (!res[0]){
                                ocultarTeclado();
                                Toast.makeText(DispositivoActivity.this, "ID ( "+auxId+" ) con ( "+marca+" ) no encontrado.\nNo se puede eliminar!!", Toast.LENGTH_SHORT).show();
                                txtid.setText("");
                                txtmarca.setText("");
                                txtdesc.setText("");
                                txtestado.setText("");

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

    private void listarDispositivos(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference(Dispositivo.class.getSimpleName());

        ArrayList<Dispositivo> listaDispositivo = new ArrayList<Dispositivo>();
        ArrayAdapter<Dispositivo> adapter = new ArrayAdapter<Dispositivo>(DispositivoActivity.this, android.R.layout.simple_list_item_1 , listaDispositivo);
        lvDatos.setAdapter(adapter);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Dispositivo dispositivo = snapshot.getValue(Dispositivo.class);
                listaDispositivo.add(dispositivo);
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
                Dispositivo dispositivo = listaDispositivo.get(position);
                AlertDialog.Builder a = new AlertDialog.Builder(DispositivoActivity.this);
                a.setCancelable(true);
                a.setTitle("Dispositivo Elegido");
                String msg = "ID : "+ dispositivo.getIdDispositivo() + "\n\n";
                msg += "Marca : " + dispositivo.getMarca()+ "\n\n";
                msg += "Descripcion : " + dispositivo.getDescripcion()+ "\n\n";
                msg += "Estado : " + dispositivo.getEstado();

                a.setMessage(msg);
                a.show();
            }
        });

    }//cierra el metodo listarUsuarios

    private void ocultarTeclado(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    } // Cierra el método ocultarTeclado.

}