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

import com.example.crudejemplo.Modelo.Permiso;
import com.example.crudejemplo.R;
import com.example.crudejemplo.databinding.ActivityPermisoBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PermisoActivity extends DrawerBaseActivity {
        private EditText txtid, txtAccion, txtDesc;
        private Button btnmod, btnreg, btneli, btnbuscaAccion, btnbuscar;
        private ListView lvDatos;
    ActivityPermisoBinding activityBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = ActivityPermisoBinding.inflate(getLayoutInflater());
        setContentView(activityBinding.getRoot());

            txtid   = findViewById(R.id.txtid);
            txtAccion = findViewById(R.id.txtAccion);
            txtDesc = findViewById(R.id.txtDescripcion);

            btnbuscar = findViewById(R.id.btnbus);
            btnbuscaAccion = findViewById(R.id.btnbusAccion);
            btnmod  = findViewById(R.id.btnmod);
            btnreg  = findViewById(R.id.btnreg);
            btneli  = findViewById(R.id.btneli);

            lvDatos = findViewById(R.id.lvDatos);

            botonBuscar();
            botonBuscarAccion();
            botonModificar();
            botonRegistrar();
            botonEliminar();
            listarPermisos();

        }   //cierra el onCreate
        private void botonBuscar(){

            btnbuscar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (txtid.getText().toString().trim().isEmpty()){
                        ocultarTeclado();
                        Toast.makeText(com.example.crudejemplo.Controladora.PermisoActivity.this, "Escriba una Identificacion para BUSCAR!!!", Toast.LENGTH_SHORT).show();

                    } else {
                        int id = Integer.parseInt(txtid.getText().toString());
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = db.getReference(Permiso.class.getSimpleName());
                        //DatabaseReference databaseReference = db.getReference().child("Permiso");
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String auxId = Integer.toString(id);
                                boolean res = false;
                                for (DataSnapshot x : snapshot.getChildren()) {

                                    if (auxId.equalsIgnoreCase(x.child("id").getValue().toString())) {
                                        res = true;
                                        ocultarTeclado();
                                        txtAccion.setText(x.child("accion").getValue().toString());
                                        txtDesc.setText(x.child("desc").getValue().toString());
                                        // Registrar la acción en la auditoría usando AuditoriaUtil
                                        AuditoriaUtil.registrarAccion("Busca Permisos por id");

                                        break;
                                    }

                                }
                                if (!res){
                                    ocultarTeclado();
                                    Toast.makeText(com.example.crudejemplo.Controladora.PermisoActivity.this, "ID ("+auxId+") no encontrado!!", Toast.LENGTH_SHORT).show();

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
        private void botonBuscarAccion(){

            btnbuscaAccion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (txtAccion.getText().toString().trim().isEmpty()){
                        ocultarTeclado();
                        Toast.makeText(com.example.crudejemplo.Controladora.PermisoActivity.this, "Escriba una Accion para BUSCAR!!!", Toast.LENGTH_SHORT).show();

                    } else {

                        String permiso = txtAccion.getText().toString();
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = db.getReference(Permiso.class.getSimpleName());
                        //DatabaseReference databaseReference = db.getReference().child("Permiso");

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                boolean res = false;
                                for (DataSnapshot x : snapshot.getChildren()) {

                                    if (permiso.equalsIgnoreCase(x.child("accion").getValue().toString())) {
                                        res = true;
                                        ocultarTeclado();
                                        txtid.setText(x.child("id").getValue().toString());
                                        txtDesc.setText(x.child("desc").getValue().toString());

                                        // Registrar la acción en la auditoría usando AuditoriaUtil
                                        AuditoriaUtil.registrarAccion("Busca Permisos por Accion");

                                        break;
                                    }

                                }
                                if (!res){
                                    ocultarTeclado();
                                    Toast.makeText(com.example.crudejemplo.Controladora.PermisoActivity.this, "Permiso ("+permiso+") no encontrado!!", Toast.LENGTH_SHORT).show();

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
                            || txtAccion.getText().toString().trim().isEmpty()
                            || txtDesc.getText().toString().trim().isEmpty()){
                        ocultarTeclado();
                        Toast.makeText(com.example.crudejemplo.Controladora.PermisoActivity.this, "Campos en blanco, completar para Modificar", Toast.LENGTH_SHORT).show();

                    }else{
                        int id = Integer.parseInt(txtid.getText().toString());
                        String accion = txtAccion.getText().toString();
                        String desc = txtDesc.getText().toString();

                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = db.getReference(Permiso.class.getSimpleName());
                        //DatabaseReference databaseReference = db.getReference().child("Permiso");
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String auxid= Integer.toString(id);
                                boolean res = false;
                                for (DataSnapshot tabla: snapshot.getChildren()){

                                    if (tabla.child("id").getValue().toString().equals(auxid)){
                                        res = true;
                                        ocultarTeclado();
                                        tabla.getRef().child("accion").setValue(accion);
                                        tabla.getRef().child("desc").setValue(desc);

                                        // Registrar la acción en la auditoría usando AuditoriaUtil
                                        AuditoriaUtil.registrarAccion("Modifica Permisos");

                                        Toast.makeText(com.example.crudejemplo.Controladora.PermisoActivity.this, "Registro modificado exitosamente!!", Toast.LENGTH_SHORT).show();
                                        txtid.setText("");
                                        txtAccion.setText("");
                                        txtDesc.setText("");
                                        listarPermisos();
                                        break;
                                    }
                                }

                                if(!res){

                                    ocultarTeclado();
                                    Toast.makeText(com.example.crudejemplo.Controladora.PermisoActivity.this, "Identificacion ("+auxid+") No encontrado.\nNo se puede modificar", Toast.LENGTH_SHORT).show();
                                    txtid.setText("");
                                    txtAccion.setText("");
                                    txtDesc.setText("");

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
                            || txtAccion.getText().toString().trim().isEmpty()
                            || txtDesc.getText().toString().trim().isEmpty()){
                        ocultarTeclado();
                        Toast.makeText(com.example.crudejemplo.Controladora.PermisoActivity.this, "Campos en blanco, completar!!", Toast.LENGTH_SHORT).show();
                    }else{
                        int id = Integer.parseInt(txtid.getText().toString());
                        String accion = txtAccion.getText().toString();
                        String desc = txtDesc.getText().toString();

                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        //DatabaseReference databaseReference = db.getReference().child("Permiso");
                        DatabaseReference databaseReference = db.getReference(Permiso.class.getSimpleName());

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String auxid= Integer.toString(id);
                                boolean res = false;
                                for (DataSnapshot tabla: snapshot.getChildren()){

                                    if (tabla.child("id").getValue().toString().equals(auxid)){
                                        res = true;
                                        ocultarTeclado();
                                        Toast.makeText(com.example.crudejemplo.Controladora.PermisoActivity.this, "Registro("+auxid+") ya existente", Toast.LENGTH_SHORT).show();
                                        break;
                                    }

                                }
                                boolean res2 = false;
                                for (DataSnapshot x : snapshot.getChildren()){

                                    if (x.child("accion").getValue().toString().equals(accion)){
                                        res2 = true;
                                        ocultarTeclado();
                                        Toast.makeText(com.example.crudejemplo.Controladora.PermisoActivity.this, "La Accion ("+accion+") ya existe", Toast.LENGTH_SHORT).show();
                                        break;
                                    }

                                }
                                if(!res && !res2){

                                    Permiso tipoPersona = new Permiso(id, accion, desc);
                                    databaseReference.push().setValue(tipoPersona);
                                    ocultarTeclado();

                                    // Registrar la acción en la auditoría usando AuditoriaUtil
                                    AuditoriaUtil.registrarAccion("Registra Permisos");

                                    Toast.makeText(com.example.crudejemplo.Controladora.PermisoActivity.this, "Permiso registrado correctamente", Toast.LENGTH_SHORT).show();
                                    txtid.setText("");
                                    txtAccion.setText("");
                                    txtDesc.setText("");

                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(com.example.crudejemplo.Controladora.PermisoActivity.this, "Error al intentar registrar:" + error , Toast.LENGTH_SHORT).show();
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
                            txtAccion.getText().toString().trim().isEmpty()){
                        ocultarTeclado();
                        Toast.makeText(com.example.crudejemplo.Controladora.PermisoActivity.this, "Buscar una Identificacion o una Accion para ELIMINAR!!!", Toast.LENGTH_SHORT).show();

                    } else {
                        int id = Integer.parseInt(txtid.getText().toString());
                        String accion = txtAccion.getText().toString();
                        String desc = txtDesc.getText().toString();
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = db.getReference(Permiso.class.getSimpleName());
                        //DatabaseReference databaseReference = db.getReference().child("Permiso");
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String auxId = Integer.toString(id);
                                final boolean[] res = {false};
                                for (DataSnapshot x : snapshot.getChildren()) {

                                    if (auxId.equalsIgnoreCase(x.child("id").getValue().toString())
                                            || accion.equalsIgnoreCase(x.child("accion").getValue().toString())) {

                                        AlertDialog.Builder a = new AlertDialog.Builder(com.example.crudejemplo.Controladora.PermisoActivity.this);
                                        a.setCancelable(false);
                                        a.setTitle("ATENCION");
                                        a.setMessage("Estas por ELIMINAR el registro..");
                                        res[0] = true;
                                        Toast.makeText(com.example.crudejemplo.Controladora.PermisoActivity.this, "ID ( "+auxId+" ) con ( "+accion+" ) encontrado.\nUsted puede eliminar!!", Toast.LENGTH_SHORT).show();
                                        a.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        a.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ocultarTeclado();
                                                Toast.makeText(com.example.crudejemplo.Controladora.PermisoActivity.this, "Registro eliminado correctamente!!", Toast.LENGTH_SHORT).show();

                                                // Registrar la acción en la auditoría usando AuditoriaUtil
                                                AuditoriaUtil.registrarAccion("Elimina Permisos");

                                                x.getRef().removeValue();
                                                listarPermisos();
                                                txtid.setText("");
                                                txtAccion.setText("");
                                                txtDesc.setText("");
                                            }
                                        });
                                        a.show();
                                        break;
                                    }
                                }
                                if (!res[0]){
                                    ocultarTeclado();
                                    Toast.makeText(com.example.crudejemplo.Controladora.PermisoActivity.this, "ID ( "+auxId+" ) con ( "+accion+" ) no encontrado.\nNo se puede eliminar!!", Toast.LENGTH_SHORT).show();
                                    txtid.setText("");
                                    txtAccion.setText("");
                                    txtDesc.setText("");
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

        private void listarPermisos(){
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            //DatabaseReference reference = db.getReference(Permiso.class.getSimpleName());
            DatabaseReference reference = db.getReference().child("Permiso");

            ArrayList<Permiso> listPermiso = new ArrayList<Permiso>();
            ArrayAdapter<Permiso> adapter = new ArrayAdapter<Permiso>(com.example.crudejemplo.Controladora.PermisoActivity.this, android.R.layout.simple_list_item_1 , listPermiso);
            lvDatos.setAdapter(adapter);

            reference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Permiso permiso= snapshot.getValue(Permiso.class);
                    listPermiso.add(permiso);
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
                    Permiso permiso = listPermiso.get(position);
                    AlertDialog.Builder a = new AlertDialog.Builder(com.example.crudejemplo.Controladora.PermisoActivity.this);
                    a.setCancelable(true);
                    a.setTitle("Permiso Elegido");
                    String msg = "ID : "+ permiso.getId() + "\n\n";
                    msg += "Tipo : " + permiso.getAccion()+ "\n\n";
                    msg += "Descripcion : " + permiso.getDesc();

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
