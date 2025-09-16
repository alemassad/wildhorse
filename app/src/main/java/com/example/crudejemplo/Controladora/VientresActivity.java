package com.example.crudejemplo.Controladora;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.crudejemplo.Modelo.Vientre;
import com.example.crudejemplo.R;
import com.example.crudejemplo.databinding.ActivityVientresBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class VientresActivity extends DrawerBaseActivity {

    private EditText txtid, txtParto, txtObserva, txtEstado;
    private Button btnbus, btnmod, btnreg, btneli, btnParto, btnPrenado;
    private ListView lvDatos;
    private DatePickerDialog datePickerDialogInicio, openDatePickerParto;
    private String date, date2;
    private EditText txtInicio;
    ActivityVientresBinding activityBinding;
    private LocalDate fechaParto, fechaInicio;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy", new Locale("es"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = ActivityVientresBinding.inflate(getLayoutInflater());
        setContentView(activityBinding.getRoot());

        txtid = findViewById(R.id.txtid);
        txtParto = findViewById(R.id.txtFechaParto);
        txtObserva = findViewById(R.id.txtObservaciones);
        txtEstado = findViewById(R.id.txtEstado);
        btnbus = findViewById(R.id.btnbus);
        btnmod = findViewById(R.id.btnmod);
        btnreg = findViewById(R.id.btnreg);
        btneli = findViewById(R.id.btneli);
        lvDatos = findViewById(R.id.lvDatos);
        txtInicio = findViewById(R.id.textInicio);
        btnParto = findViewById(R.id.fechaPartoButton);
        btnParto.setText(getFechaActual());
        btnPrenado = findViewById(R.id.fechaButton);
        btnPrenado.setText(getFechaActual());

        initDatePicker();
        initDatePicker2();
        botonBuscar();
        botonModificar();
        botonRegistrar();
        botonEliminar();
        listarVientres();

        }   //cierra el onCreate

    //Metodo para ingresar la fecha actual de embaraso
    private String getFechaActual() {
        Calendar cal = Calendar.getInstance();
        int ano = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        mes = mes + 1;
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(dia, mes, ano);
    }
    // Ejemplo de uso en el DatePicker
    private void initDatePicker() {
        Log.e("dentro del initDATEPICKER", "dentro del initDATEPICKER");
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
                mes = mes + 1;
                date = makeDateString(dia, mes, ano);
                btnPrenado.setText(date);
                txtInicio.setText(date);
                // Calcular la fecha de parto
                fechaInicio = LocalDate.of(ano, mes, dia);
                calcularFechaParto(fechaInicio);
            }
        };
        Calendar cal = Calendar.getInstance();
        int ano = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);

        int style = android.app.AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialogInicio = new DatePickerDialog(this, style, dateSetListener, ano, mes, dia);
        datePickerDialogInicio.getDatePicker().setMaxDate(System.currentTimeMillis());
           }
    private void initDatePicker2() {
        Log.e("dentro del initDATEPICKER2", "dentro del initDATEPICKER 2");
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
                mes = mes + 1;
                date2 = makeDateString(dia, mes, ano);
                btnParto.setText(date2);
                txtParto.setText(date2);
                fechaParto = LocalDate.of(ano, mes, dia);
                calcularFechaInicio(fechaParto);
            }
        };
        Calendar cal = Calendar.getInstance();
        int ano = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        int style = android.app.AlertDialog.THEME_HOLO_LIGHT;
        openDatePickerParto = new DatePickerDialog(this, style, dateSetListener, ano, mes, dia+1);
        openDatePickerParto.getDatePicker().setMinDate(System.currentTimeMillis());
    }
    private String makeDateString(int dia, int mes, int ano) {
        return getMonthFormat(mes) + " " + dia + " " + ano;
    }
    private String getMonthFormat(int mes) {
        if (mes == 1)
            return "ene";
        if (mes == 2)
            return "feb";
        if (mes == 3)
            return "mar";
        if (mes == 4)
            return "abr";
        if (mes == 5)
            return "may";
        if (mes == 6)
            return "jun";
        if (mes == 7)
            return "jul";
        if (mes == 8)
            return "ago";
        if (mes == 9)
            return "set";
        if (mes == 10)
            return "oct";
        if (mes == 11)
            return "nov";
        if (mes == 12)
            return "dic";
        //Default
        return "ene";
    }
    public void openDatePicker(View view) {
        datePickerDialogInicio.show();
    }// fin de ingresar fecha de embaraso
    public void openDatePickerParto(View view) {
        openDatePickerParto.show();
    }
    // Método para convertir String a LocalDate
    private LocalDate convertirAFecha(String fechaString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy", new Locale("es"));
            return LocalDate.parse(fechaString, formatter);
        } catch (DateTimeParseException e) {
            // Manejar el error si la fecha no tiene el formato correcto
            Log.e("VientresActivity", "Error al convertir fecha: " + fechaString, e);
            return null; // Devuelve null en caso de error
        }
    }

    // Nueva función calcularFechaParto
    private void calcularFechaParto(LocalDate inicio) {
        if (inicio == null) {
            Toast.makeText(this, "Fecha de inicio no válida", Toast.LENGTH_SHORT).show();
            return;
        }
        // Calcular la fecha de parto (320 días después)
        LocalDate fechaPartofunc = inicio.plusDays(320);

        // Formatear la fecha de parto al formato ("MMM dd yyyy")
        String partoFormateado = fechaPartofunc.format(formatter);
        //partoFormateado = partoFormateado.toUpperCase();
        // Mostrar la fecha de parto en txtParto
        txtParto.setText(partoFormateado);
        btnParto.setText(partoFormateado);
    }

    // Nueva función calcularFechaInicio
    private void calcularFechaInicio(LocalDate parto) {
        if (parto == null) {
            Toast.makeText(this, "Fecha de parto no válida", Toast.LENGTH_SHORT).show();
            return;
        }
       // Calcular la fecha de inicio (320 días antes)
        LocalDate fechaIniciofunc = parto.minusDays(320);

        // Formatear la fecha de inicio al formato ("MMM dd yyyy")
        String inicioFormateado = fechaIniciofunc.format(formatter);
        txtInicio.setText(inicioFormateado);
        btnPrenado.setText(inicioFormateado);
    }

    private void botonBuscar(){

            btnbus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (txtid.getText().toString().trim().isEmpty()){
                        ocultarTeclado();
                        Toast.makeText(com.example.crudejemplo.Controladora.VientresActivity.this, "Escriba una Identificacion para BUSCAR!!!", Toast.LENGTH_SHORT).show();

                    } else {
                        int id = Integer.parseInt(txtid.getText().toString());
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = db.getReference(Vientre.class.getSimpleName());
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String auxId = Integer.toString(id);
                                boolean res = false;
                                for (DataSnapshot x : snapshot.getChildren()) {

                                    if (auxId.equalsIgnoreCase(x.child("id").getValue().toString())) {
                                        res = true;
                                        ocultarTeclado();
                                        txtInicio.setText(x.child("fechaInicio").getValue().toString());
                                        txtParto.setText(x.child("fechaParto").getValue().toString());
                                        btnParto.setText(x.child("fechaParto").getValue().toString());
                                        txtObserva.setText(x.child("observaciones").getValue().toString());
                                        txtEstado.setText(x.child("estado").getValue().toString());

                                        // Registrar la acción en la auditoría usando AuditoriaUtil
                                        AuditoriaUtil.registrarAccion("Busca Vientres");

                                        break;
                                    }
                                }
                                if (!res){
                                    ocultarTeclado();
                                    Toast.makeText(com.example.crudejemplo.Controladora.VientresActivity.this, "ID ("+auxId+") no encontrado!!", Toast.LENGTH_SHORT).show();
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

    private void botonModificar(){

            btnmod.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (txtid.getText().toString().trim().isEmpty()
                            || txtInicio.getText().toString().trim().isEmpty()
                            || txtParto.getText().toString().trim().isEmpty()
                            || txtObserva.getText().toString().trim().isEmpty()
                            || txtEstado.getText().toString().trim().isEmpty()){
                        ocultarTeclado();
                        Toast.makeText(com.example.crudejemplo.Controladora.VientresActivity.this, "Campos en blanco, completar para Modificar", Toast.LENGTH_SHORT).show();

                    }else{
                        int id = Integer.parseInt(txtid.getText().toString());
                        String inicio = txtInicio.getText().toString();
                        String parto = txtParto.getText().toString();
                        String observa = txtObserva.getText().toString();
                        String estado = txtEstado.getText().toString();

                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = db.getReference(Vientre.class.getSimpleName());
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String auxid= Integer.toString(id);
                                boolean res = false;
                                for (DataSnapshot tabla: snapshot.getChildren()){

                                    if (tabla.child("id").getValue().toString().equals(auxid)){
                                        res = true;
                                        ocultarTeclado();
                                        tabla.getRef().child("fechaInicio").setValue(inicio);
                                        tabla.getRef().child("fechaParto").setValue(parto);
                                        tabla.getRef().child("observaciones").setValue(observa);
                                        tabla.getRef().child("estado").setValue(estado);
                                        Toast.makeText(com.example.crudejemplo.Controladora.VientresActivity.this, "Registro modificado exitosamente!!", Toast.LENGTH_SHORT).show();
                                        // Registrar la acción en la auditoría usando AuditoriaUtil
                                        AuditoriaUtil.registrarAccion("Modifica Vientres");

                                        txtid.setText("");
                                        txtInicio.setText("");
                                        txtParto.setText("");
                                        txtObserva.setText("");
                                        txtEstado.setText("");
                                        listarVientres();
                                        break;
                                    }
                                }
                                if(!res){
                                    ocultarTeclado();
                                    Toast.makeText(com.example.crudejemplo.Controladora.VientresActivity.this, "Identificacion ("+auxid+") No encontrado.\nNo se puede modificar", Toast.LENGTH_SHORT).show();
                                    txtid.setText("");
                                    txtInicio.setText("");
                                    txtParto.setText("");
                                    txtObserva.setText("");
                                    txtEstado.setText("");
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
                            || txtInicio.getText().toString().trim().isEmpty()
                            || txtObserva.getText().toString().trim().isEmpty()
                            || txtEstado.getText().toString().trim().isEmpty()){
                        ocultarTeclado();
                        Toast.makeText(com.example.crudejemplo.Controladora.VientresActivity.this, "Campos en blanco, completar!!", Toast.LENGTH_SHORT).show();

                    }else{
                        int id = Integer.parseInt(txtid.getText().toString());
                        String inicio = txtInicio.getText().toString();
                        String parto = txtParto.getText().toString();
                        String observa = txtObserva.getText().toString();
                        String estado = txtEstado.getText().toString();

                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = db.getReference(Vientre.class.getSimpleName());

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String auxid= Integer.toString(id);
                                boolean res = false;
                                for (DataSnapshot tabla: snapshot.getChildren()){

                                    if (tabla.child("id").getValue().toString().equals(auxid)){
                                        res = true;
                                        ocultarTeclado();
                                        Toast.makeText(com.example.crudejemplo.Controladora.VientresActivity.this, "Registro("+auxid+") ya existente", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                                if(!res){

                                    Vientre vientre = new Vientre(id, inicio, parto, observa, estado);
                                    databaseReference.push().setValue(vientre);
                                    // Registrar la acción en la auditoría usando AuditoriaUtil
                                    AuditoriaUtil.registrarAccion("Registra Vientres");
                                    ocultarTeclado();
                                    Toast.makeText(com.example.crudejemplo.Controladora.VientresActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                                    txtid.setText("");
                                    txtInicio.setText("");
                                    txtParto.setText("");
                                    txtObserva.setText("");
                                    txtEstado.setText("");
                                    listarVientres();
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

                    if (txtid.getText().toString().trim().isEmpty() ||
                            txtInicio.getText().toString().trim().isEmpty()){
                        ocultarTeclado();
                        Toast.makeText(com.example.crudejemplo.Controladora.VientresActivity.this, "Buscar una Identificacion o una Fecha para ELIMINAR!!!", Toast.LENGTH_SHORT).show();

                    } else {
                        int id = Integer.parseInt(txtid.getText().toString());
                        String inicio = txtInicio.getText().toString();
                        String parto = txtParto.getText().toString();
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = db.getReference(Vientre.class.getSimpleName());
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String auxId = Integer.toString(id);
                                final boolean[] res = {false};
                                for (DataSnapshot x : snapshot.getChildren()) {

                                    if (auxId.equalsIgnoreCase(x.child("id").getValue().toString())
                                            || inicio.equalsIgnoreCase(x.child("fechaInicio").getValue().toString()) || parto.equalsIgnoreCase((x.child("fechaParto").getValue().toString()))) {

                                        AlertDialog.Builder a = new AlertDialog.Builder(com.example.crudejemplo.Controladora.VientresActivity.this);
                                        a.setCancelable(false);
                                        a.setTitle("ATENCION");
                                        a.setMessage("Estas por ELIMINAR el registro..");

                                        Toast.makeText(com.example.crudejemplo.Controladora.VientresActivity.this, "ID ( "+auxId+" ) con inicio: ( "+inicio+" ) y parto ( "+parto+" )encontrado.\nUsted puede eliminar!!", Toast.LENGTH_SHORT).show();
                                        // Create a SpannableString for "Cancelar" with green color
                                        SpannableString cancelText = new SpannableString("Cancelar");
                                        cancelText.setSpan(new ForegroundColorSpan(Color.GREEN), 0, cancelText.length(), 0);
                                        a.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Acción para el botón "Cancelar"
                                            }
                                        });

                                        SpannableString deleteText = new SpannableString("Eliminar");
                                        deleteText.setSpan(new ForegroundColorSpan(Color.RED), 0, deleteText.length(), 0);

                                        a.setPositiveButton(deleteText, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ocultarTeclado();
                                                // Registrar la acción en la auditoría usando AuditoriaUtil
                                                AuditoriaUtil.registrarAccion("Elimina Vientres");

                                                x.getRef().removeValue();
                                                listarVientres();
                                                txtid.setText("");
                                                txtInicio.setText("");
                                                txtParto.setText("");
                                                txtObserva.setText("");
                                                txtEstado.setText("");
                                            }
                                        });

                                        AlertDialog dialog = a.show();

                                        break;
                                    }
                                }
                                if (!res[0]){
                                    ocultarTeclado();
                                    Toast.makeText(com.example.crudejemplo.Controladora.VientresActivity.this, "ID ( "+auxId+" ) con inicio: ( "+inicio+" ) y parto ( "+parto+" ) no encontrado.\nUsted no puede eliminar!!", Toast.LENGTH_SHORT).show();
                                    txtid.setText("");
                                    txtInicio.setText("");
                                    txtParto.setText("");
                                    txtObserva.setText("");
                                    txtEstado.setText("");

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

    private void listarVientres() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference(Vientre.class.getSimpleName());
        ArrayList<Vientre> listaVientre = new ArrayList<>();
        ArrayAdapter<Vientre> adapter = new ArrayAdapter<>(com.example.crudejemplo.Controladora.VientresActivity.this, android.R.layout.simple_list_item_1, listaVientre);
        lvDatos.setAdapter(adapter);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaVientre.clear(); // Limpiar la lista antes de agregar nuevos datos
                LocalDate fechaActual = LocalDate.now(); // Obtener la fecha actual

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Vientre vientre = dataSnapshot.getValue(Vientre.class);
                    listaVientre.add(vientre);

                    // Verificar si la fecha de parto es igual o menor a la fecha actual
                    LocalDate fechaParto = convertirAFecha(vientre.getFechaParto());
                    if (fechaParto != null && !fechaParto.isAfter(fechaActual)) {
                        //mostrarAlertaFechaParto(vientre); // Mostrar alerta si la fecha de parto es igual o anterior
                        mostrarNotificacionEnBarraDeEstado(vientre);
                    }
                }

                // Ordenar la lista por fecha de parto
                Collections.sort(listaVientre, new Comparator<Vientre>() {
                    @Override
                    public int compare(Vientre v1, Vientre v2) {
                        LocalDate fechaParto1 = convertirAFecha(v1.getFechaParto());
                        LocalDate fechaParto2 = convertirAFecha(v2.getFechaParto());

                        if (fechaParto1 == null && fechaParto2 == null) {
                            return 0;
                        } else if (fechaParto1 == null) {
                            return 1; // Nulls al final
                        } else if (fechaParto2 == null) {
                            return -1;
                        }

                        return fechaParto1.compareTo(fechaParto2);
                    }

                });

                adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar el error si es necesario
                Log.e("VientresActivity", "Error al obtener datos de Firebase", error.toException());
            }
        });

        lvDatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Vientre vientre = listaVientre.get(position);
                AlertDialog.Builder a = new AlertDialog.Builder(com.example.crudejemplo.Controladora.VientresActivity.this);
                a.setCancelable(true);
                a.setTitle("Vientre Elegido");
                String msg = "ID : " + vientre.getId() + "\n\n";
                msg += "FechaInicio : " + vientre.getFechaInicio() + "\n\n";
                msg += "FechaParto :<font color='#FFA500'> " + vientre.getFechaParto() + "</font><br><br>";
                msg += "Observaciones : " + vientre.getObservaciones() + "\n\n";
                msg += "Estado : " + vientre.getEstado();

                // Mostrar el mensaje en el cuadro de diálogo
                a.setMessage(Html.fromHtml(msg, Html.FROM_HTML_MODE_LEGACY));
                a.show();
            }
        });
    }
    private void mostrarNotificacionEnBarraDeEstado(Vientre vientre) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "fecha_parto_channel";

        // Crear un canal de notificación (requerido para Android 8.0 y superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Fecha de Parto",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Crear la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ico_wildhorse) // Icono de la notificación
                .setContentTitle("Alerta de Fecha de Parto")
                .setContentText("La llegua con ID " + vientre.getId() + " está pronta a parir.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Mostrar la notificación
        notificationManager.notify(vientre.getId(), builder.build());
    }

    private void ocultarTeclado(){
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } // Cierra el método ocultarTeclado.

}   //cierra la clase