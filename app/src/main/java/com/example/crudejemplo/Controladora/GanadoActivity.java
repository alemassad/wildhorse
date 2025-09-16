package com.example.crudejemplo.Controladora;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.media3.common.util.UnstableApi;

import com.example.crudejemplo.Modelo.Ganado;
import com.example.crudejemplo.Modelo.Raza;
import com.example.crudejemplo.R;
import com.example.crudejemplo.databinding.ActivityGanadoBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GanadoActivity extends DrawerBaseActivity {

    private EditText txtid, txtsexo, txtraza, txttemp, txtestado;
    private Button btnbus, btnmod, btnreg, btneli, btnbusSexo, btnbusRaza;
    private ListView lvDatos;
    private String dispositivo;
    private String vientre;

    private int index;
    ActivityGanadoBinding  activityBinding;
    Spinner spinnerRaza;
    Spinner spinnerDispositivo;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = ActivityGanadoBinding.inflate(getLayoutInflater());
        setContentView(activityBinding.getRoot());

        txtid   = findViewById(R.id.txtid);
        txtsexo  = findViewById(R.id.txtSexo);
        txtraza   = findViewById(R.id.txtRaza);
        //txtvientre   = findViewById(R.id.txtVientre);
        txttemp   = findViewById(R.id.txtTemperatura);
        txtestado   = findViewById(R.id.txtestado);

        btnbus  = findViewById(R.id.btnbus);
        btnmod  = findViewById(R.id.btnmod);
        btnreg  = findViewById(R.id.btnreg);
        btneli  = findViewById(R.id.btneli);
        lvDatos = findViewById(R.id.lvDatos);
        //btnbusSexo = findViewById(R.id.btnBusSexo);
        //btnbusRaza = findViewById(R.id.btnbusRaza);
        spinnerRaza = findViewById(R.id.spinner_raza);
        //spinnerDispositivo = findViewById(R.id.spinner_dispositivo);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Deshabilitamos los txt que tienen spinner
        txtraza.setEnabled(false);
        //txtDispo.setEnabled(false);


        //Inicializamos los botones, la lista y los spinner
        botonBuscar();
        //botonBuscarSexo();
        //botonBuscarRaza();
        botonModificar();
        botonRegistrar();
        botonEliminar();
        listarGanados();
        LoadRazas();
        //LoadDispositivos();


    }   //cierra el onCreate

    // Spinner de razas, metodo para cargar
    @OptIn(markerClass = UnstableApi.class)
    public void LoadRazas() {
        List<Raza> razas = new ArrayList<>();
        Log.e("LoadRazas", "raza: " + razas);

        databaseReference.child("Raza").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        // Obtener los valores de los campos
                        String nombreRaza = ds.child("nombre").getValue(String.class);

                        // Agregar el grupo a la lista
                        razas.add(new Raza(nombreRaza));
                    }

                    // Crear el adaptador para el Spinner
                    ArrayAdapter<Raza> adapter = new ArrayAdapter<>(GanadoActivity.this, android.R.layout.simple_dropdown_item_1line, razas); // Lista de objetos Raza
                    adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice); // Layout para la vista desplegable
                    spinnerRaza.setAdapter(adapter);

                    // Establecer la selección del Spinner según el índice
                    spinnerRaza.setSelection(index);

                    spinnerRaza.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Raza razaseleccionada = (Raza) parent.getSelectedItem();

                            // Solo actualizar el texto de txtGrupo si no es el valor por defecto (índice 3)
                            if (razaseleccionada != null) {
                                txtraza.setText(razaseleccionada.getNombre()); // Mostrar el nombre de la raza en txtRaza
                            } else {
                                txtraza.setText(""); // Limpiar el texto si el índice es 3
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
                Log.e("LoadRazas", "Error al cargar razas: " + error.getMessage());
            }
        });

    }

    private void botonBuscar(){

        btnbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtid.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(GanadoActivity.this, "Escriba una Identificacion para BUSCAR!!!", Toast.LENGTH_SHORT).show();

                } else {
                    int id = Integer.parseInt(txtid.getText().toString());
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Ganado.class.getSimpleName());
                    // DatabaseReference databaseReference = db.getReference().child("Usuario");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String auxId = Integer.toString(id);
                            boolean res = false;
                            for (DataSnapshot x : snapshot.getChildren()) {

                                if (auxId.equalsIgnoreCase(x.child("id").getValue().toString())) {
                                    res = true;
                                    ocultarTeclado();
                                    txtsexo.setText(x.child("sexo").getValue().toString());
                                    txtraza.setText(x.child("raza").getValue().toString());
                                    txttemp.setText(x.child("temperatura").getValue().toString());
                                    txtestado.setText(x.child("estado").getValue().toString());
                                    dispositivo = (x.child("dispositivo").getValue().toString());
                                    vientre = x.child("vientre").getValue().toString();

                                    // Seleccionar el RadioButton correspondiente
                                    RadioGroup radioGroupDispositivo = findViewById(R.id.radioGroup_dispositivo);
                                    if (dispositivo.equalsIgnoreCase("SI")) {
                                        ((RadioButton) findViewById(R.id.rbSidispositivo)).setChecked(true);
                                    } else if (dispositivo.equalsIgnoreCase("NO")) {
                                        ((RadioButton) findViewById(R.id.rbNodispositivo)).setChecked(true);
                                    }

                                    // Obtener y asignar el valor de vientre (Sí o No)
                                    RadioGroup radioGroupVientre = findViewById(R.id.radioGroup_vientre);
                                    if (vientre.equalsIgnoreCase("SI")) {
                                        ((RadioButton) findViewById(R.id.rbSi_vientre)).setChecked(true);
                                    } else if (vientre.equalsIgnoreCase("NO")) {
                                        ((RadioButton) findViewById(R.id.rbNo_vientre)).setChecked(true);
                                    }

                                    // Registrar accion en la auditoría
                                    AuditoriaUtil.registrarAccion("Busca Ganado por ID");

                                    break;
                                }

                            }
                            if (!res){
                                ocultarTeclado();
                                Toast.makeText(GanadoActivity.this, "ID ("+auxId+") no encontrado!!", Toast.LENGTH_SHORT).show();

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

    /*private void botonBuscarRaza(){

        btnbusRaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtraza.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(GanadoActivity.this, "Escriba una Raza para BUSCAR!!!", Toast.LENGTH_SHORT).show();

                } else {

                    String raza = txtraza.getText().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Ganado.class.getSimpleName());
                    // DatabaseReference databaseReference = db.getReference().child("Ganados");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            boolean res = false;
                            for (DataSnapshot x : snapshot.getChildren()) {

                                if (raza.equalsIgnoreCase(x.child("raza").getValue().toString())) {
                                    res = true;
                                    ocultarTeclado();
                                    txtid.setText(x.child("id").getValue().toString());
                                    txtsexo.setText(x.child("sexo").getValue().toString());
                                    txtDispo.setText(x.child("dispositivo").getValue().toString());
                                    txtvientre.setText(x.child("vientre").getValue().toString());
                                    txttemp.setText(x.child("temperatura").getValue().toString());
                                    txtestado.setText(x.child("estado").getValue().toString());
                                    break;
                                }

                            }
                            if (!res){
                                ocultarTeclado();
                                Toast.makeText(GanadoActivity.this, "Raza ("+raza+") no encontrada!!", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } //cierra el if/else
            }
        });

    }*///cierra el metodo boton buscar x Raza

    private void botonModificar(){

        btnmod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtid.getText().toString().trim().isEmpty()
                        || txtsexo.getText().toString().trim().isEmpty()
                        || txtraza.getText().toString().trim().isEmpty()
                        || txttemp.getText().toString().trim().isEmpty()
                        || txtestado.getText().toString().trim().isEmpty())
                
                {
                    ocultarTeclado();
                    Toast.makeText(GanadoActivity.this, "Campos en blanco, completar para Modificar", Toast.LENGTH_SHORT).show();
                    return;
                }
                RadioGroup radioGroup_dispositivo = findViewById(R.id.radioGroup_dispositivo);
                RadioGroup radioGroup_vientre = findViewById(R.id.radioGroup_vientre);
                int selectedId_dispositivo = radioGroup_dispositivo.getCheckedRadioButtonId();
                int selectedId_vientre = radioGroup_vientre.getCheckedRadioButtonId();

                if (selectedId_dispositivo == -1) {
                    Toast.makeText(GanadoActivity.this, "Selecciona una opción para el dispositivo", Toast.LENGTH_SHORT).show();
                } else if (selectedId_vientre == -1) {
                    Toast.makeText(GanadoActivity.this, "Selecciona una opción para el vientre", Toast.LENGTH_SHORT).show();
                }
                    int id = Integer.parseInt(txtid.getText().toString());
                    String sexo = txtsexo.getText().toString();
                    String raza = txtraza.getText().toString();
                    String temp = txttemp.getText().toString();
                    String estado = txtestado.getText().toString();

                    // Asignar valor a la variable 'dispositivo' según el RadioButton seleccionado
                    dispositivo = (selectedId_dispositivo == R.id.rbSidispositivo) ? "SI" : "NO";

                    // Asignar valor a la variable 'vientre' según el RadioButton seleccionado
                    vientre = (selectedId_vientre == R.id.rbSi_vientre) ? "SI" : "NO";

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Ganado.class.getSimpleName());

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String auxid= Integer.toString(id);
                            boolean res = false;
                            for (DataSnapshot tabla: snapshot.getChildren()){

                                if (tabla.child("id").getValue().toString().equals(auxid)){
                                    res = true;
                                    ocultarTeclado();
                                    tabla.getRef().child("sexo").setValue(sexo);
                                    tabla.getRef().child("raza").setValue(raza);
                                    tabla.getRef().child("dispositivo").setValue(dispositivo);
                                    tabla.getRef().child("vientre").setValue(vientre);
                                    tabla.getRef().child("temperatura").setValue(temp);
                                    tabla.getRef().child("estado").setValue(estado);

                                    // Registrar accion en la auditoría
                                    AuditoriaUtil.registrarAccion("Modifica Ganado");

                                    Toast.makeText(com.example.crudejemplo.Controladora.GanadoActivity.this, "Registro modificado exitosamente!!", Toast.LENGTH_SHORT).show();
                                    txtid.setText("");
                                    txtsexo.setText("");
                                    txtraza.setText("");
                                    txttemp.setText("");
                                    txtestado.setText("");
                                    listarGanados();
                                    break;
                                }
                            }

                            if(!res){
                                ocultarTeclado();
                                Toast.makeText(GanadoActivity.this, "Identificacion ("+auxid+") No encontrado.\nNo se puede modificar", Toast.LENGTH_SHORT).show();
                                txtid.setText("");
                                txtsexo.setText("");
                                txtraza.setText("");
                                txttemp.setText("");
                                txtestado.setText("");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                //Cierra el if else

            }
        });

    } //cierra el metodo boton modificar

    private void botonRegistrar(){
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtid.getText().toString().trim().isEmpty()
                        || txtsexo.getText().toString().trim().isEmpty()
                        || txtraza.getText().toString().trim().isEmpty()
                        || txttemp.getText().toString().trim().isEmpty()
                        || txtestado.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(GanadoActivity.this, "Campos en blanco, completar!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Obtener el valor del RadioButton seleccionado
                RadioGroup radioGroup_dispositivo = findViewById(R.id.radioGroup_dispositivo);
                RadioGroup radioGroup_vientre = findViewById(R.id.radioGroup_vientre);

                int selectedId_dispositivo = radioGroup_dispositivo.getCheckedRadioButtonId();
                int selectedId_vientre = radioGroup_vientre.getCheckedRadioButtonId();

                if (selectedId_dispositivo == -1) {
                    Toast.makeText(GanadoActivity.this, "Selecciona una opción para el dispositivo", Toast.LENGTH_SHORT).show();
                } else if (selectedId_vientre == -1) {
                    Toast.makeText(GanadoActivity.this, "Selecciona una opción para el vientre", Toast.LENGTH_SHORT).show();
                } else{
                    int id = Integer.parseInt(txtid.getText().toString());
                    String sexo = txtsexo.getText().toString();
                    String raza = txtraza.getText().toString();
                    String temp = txttemp.getText().toString();
                    String estado = txtestado.getText().toString();
                    if (selectedId_dispositivo == R.id.rbSidispositivo) {
                        dispositivo = "SI";
                    } else if (selectedId_dispositivo == R.id.rbNodispositivo) {
                        dispositivo = "NO";
                    }
                    if (selectedId_vientre == R.id.rbSi_vientre){
                        vientre = "SI";
                    } else if (selectedId_vientre == R.id.rbNo_vientre) {
                        vientre = "NO";
                    }

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Ganado.class.getSimpleName());
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String auxid= Integer.toString(id);
                            boolean res = false;
                            for (DataSnapshot tabla: snapshot.getChildren()){

                                if (tabla.child("id").getValue().toString().equals(auxid)){
                                    res = true;
                                    ocultarTeclado();
                                    Toast.makeText(GanadoActivity.this, "Registro("+auxid+") ya existe", Toast.LENGTH_SHORT).show();
                                    break;
                                }

                            }

                            if(!res){

                                Ganado ganado = new Ganado(id, sexo, raza, dispositivo, vientre, temp, estado);
                                databaseReference.push().setValue(ganado);
                                ocultarTeclado();

                                // Registrar accion en la auditoría
                                AuditoriaUtil.registrarAccion("Registra Ganado");

                                Toast.makeText(GanadoActivity.this, "Ganado registrado correctamente", Toast.LENGTH_SHORT).show();
                                txtid.setText("");
                                txtsexo.setText("");
                                txtraza.setText("");
                                txttemp.setText("");
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

    }//Cierra el metodo boton registrar
    private void botonEliminar(){
        btneli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtid.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(GanadoActivity.this, "Buscar una Identificacion para ELIMINAR!!!", Toast.LENGTH_SHORT).show();

                } else {
                    int id = Integer.parseInt(txtid.getText().toString());
                    String raza = txtraza.getText().toString();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = db.getReference(Ganado.class.getSimpleName());

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String auxId = Integer.toString(id);
                            final boolean[] res = {false};
                            for (DataSnapshot x : snapshot.getChildren()) {

                                if (auxId.equalsIgnoreCase(x.child("id").getValue().toString())) {

                                    AlertDialog.Builder a = new AlertDialog.Builder(GanadoActivity.this);
                                    a.setCancelable(false);
                                    a.setTitle("ATENCION");
                                    a.setMessage("Estas por ELIMINAR el registro..");
                                    res[0] = true;
                                    Toast.makeText(com.example.crudejemplo.Controladora.GanadoActivity.this, "ID ( "+auxId+" ) con ( "+raza+" ) encontrado.\nUsted puede eliminar!!", Toast.LENGTH_SHORT).show();
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

                                            // Registrar accion en la auditoría
                                            AuditoriaUtil.registrarAccion("Elimina Ganado");

                                            listarGanados();
                                            txtid.setText("");
                                            txtsexo.setText("");
                                            txtraza.setText("");
                                            txttemp.setText("");
                                            txtestado.setText("");
                                        }
                                    });
                                    a.show();
                                    break;
                                }
                            }
                            if (!res[0]){
                                ocultarTeclado();
                                Toast.makeText(GanadoActivity.this, "ID ( "+auxId+" ) con ( "+raza+" ) no encontrada.\nNo se puede eliminar!!", Toast.LENGTH_SHORT).show();
                                txtid.setText("");
                                txtsexo.setText("");
                                txtraza.setText("");
                                txttemp.setText("");
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

    private void listarGanados(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference(Ganado.class.getSimpleName());

        ArrayList<Ganado> listaGanado = new ArrayList<Ganado>();
        ArrayAdapter<Ganado> adapter = new ArrayAdapter<Ganado>(GanadoActivity.this, android.R.layout.simple_list_item_1 , listaGanado);
        lvDatos.setAdapter(adapter);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Ganado ganado = snapshot.getValue(Ganado.class);
                listaGanado.add(ganado);
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
                Ganado ganado = listaGanado.get(position);
                AlertDialog.Builder a = new AlertDialog.Builder(GanadoActivity.this);
                a.setCancelable(true);
                a.setTitle("Ganado Elegido");
                String msg = "ID : "+ ganado.getId() + "\n\n";
                msg += "Sexo : " + ganado.getSexo()+ "\n\n";
                msg += "Raza : " + ganado.getRaza()+ "\n\n";
                msg += "Dispositivo : " + ganado.getDispositivo()+ "\n\n";
                msg += "Vientre : " + ganado.getVientre()+ "\n\n";
                msg += "Temperatura : " + ganado.getTemperatura()+ "\n\n";
                msg += "Estado : " + ganado.getEstado();

                a.setMessage(msg);
                a.show();
            }
        });

    }//cierra el metodo listarGanados

    private void ocultarTeclado(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    } // Cierra el método ocultarTeclado.


}   //cierra la clase