package com.example.crudejemplo.Controladora;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.crudejemplo.Modelo.Ganado;
import com.example.crudejemplo.Modelo.Temperatura;
import com.example.crudejemplo.Modelo.Vientre;
import com.example.crudejemplo.R;
import com.example.crudejemplo.databinding.ActivityDashboardBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class DashboardActivity extends DrawerBaseActivity {
    ActivityDashboardBinding activityBinding;
    private WebView analyticsWebView;

    private DatabaseReference databaseReference, databaseReferenceVientre, databaseReferenceTemperatura;
    private List<Ganado> listaGanado = new ArrayList<>();
    private List<Temperatura> listaTemperatura = new ArrayList<>();
    private List<Vientre> listaVientres = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(activityBinding.getRoot());

        // Registrar accion en la auditoría
        AuditoriaUtil.registrarAccion("Visualiza estadisticas");

       /* analyticsWebView = findViewById(R.id.analyticsWebView);
        analyticsWebView.getSettings().setJavaScriptEnabled(true);
        analyticsWebView.setWebViewClient(new WebViewClient());
        analyticsWebView.loadUrl("https://console.firebase.google.com/u/0/project/crudeje-a10ae/overview"); // Aqui va tu URL de Firebase Analytics*/

        // Inicializar Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Ganado");

        // Obtener datos de Firebase
        obtenerDatosDeGanado();
        databaseReferenceTemperatura = FirebaseDatabase.getInstance().getReference("Temperatura");
        obtenerDatosDeTemperatura();
        // Inicializar Firebase para la tabla Vientre
        databaseReferenceVientre = FirebaseDatabase.getInstance().getReference("Vientre");
        obtenerDatosDeVientres();
    }
    private void obtenerDatosDeVientres() {
        databaseReferenceVientre.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaVientres.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Vientre vientre = dataSnapshot.getValue(Vientre.class);
                    listaVientres.add(vientre);
                }
                // Una vez obtenidos los datos, generar el gráfico de vientres
                generarGraficoVientres();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error al obtener datos de Vientres", error.toException());
            }
        });
    }
    private Map<String, Integer> contarVientresPorEstado() {
        Map<String, Integer> vientresPorEstado = new HashMap<>();
        for (Vientre vientre : listaVientres) {
            String estado = vientre.getEstado();
            vientresPorEstado.put(estado, vientresPorEstado.getOrDefault(estado, 0) + 1);
        }
        return vientresPorEstado;
    }
    private void generarGraficoVientres() {
        // Obtener datos procesados
        Map<String, Integer> vientresPorEstado = contarVientresPorEstado();

        // Crear entradas para el gráfico de barras
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Integer> entry : vientresPorEstado.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue())); // index es el eje X, entry.getValue() es el eje Y
            labels.add(entry.getKey()); // Etiquetas para el eje X
            index++;
        }

        // Configurar el gráfico de barras
        BarDataSet dataSet = new BarDataSet(entries, "Vientres por Estado");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Colores para las barras
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f); // Ancho de las barras

        BarChart barChart = findViewById(R.id.barChartVientres);
        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels)); // Etiquetas en el eje X
        barChart.getXAxis().setGranularity(2f); // Espaciado en el eje X
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.setFitBars(true); // Ajustar las barras al gráfico
        barChart.getDescription().setEnabled(false); // Ocultar descripción
        barChart.invalidate(); // Refrescar el gráfico
    }

    private void obtenerDatosDeTemperatura() {
        databaseReferenceTemperatura.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaTemperatura.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Temperatura temperatura = dataSnapshot.getValue(Temperatura.class);
                    listaTemperatura.add(temperatura);
                }
                // Una vez obtenidos los datos, generar gráficos
                generarGraficoTemperatura();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error al obtener datos", error.toException());
            }
        });
    }
        private void obtenerDatosDeGanado () {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listaGanado.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Ganado ganado = dataSnapshot.getValue(Ganado.class);
                        listaGanado.add(ganado);
                    }
                    // Una vez obtenidos los datos, generar gráficos
                    generarGraficos();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Error al obtener datos", error.toException());
                }
            });

        }
        private void generarGraficos () {
            // Obtener datos procesados
            Map<String, Integer> ganadoPorRaza = contarGanadoPorRaza();

            // Crear entradas para el gráfico de barras
            List<BarEntry> entries = new ArrayList<>();
            List<String> labels = new ArrayList<>();
            int index = 0;
            for (Map.Entry<String, Integer> entry : ganadoPorRaza.entrySet()) {
                entries.add(new BarEntry(index, entry.getValue()));
                labels.add(entry.getKey());
                index++;
            }

            // Configurar el gráfico de barras
            BarDataSet dataSet = new BarDataSet(entries, "Ganado por Raza");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            BarData barData = new BarData(dataSet);
            barData.setBarWidth(0.9f); // Ancho de las barras

            BarChart barChart = findViewById(R.id.barChart);
            barChart.setData(barData);
            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
            barChart.getXAxis().setGranularity(1f);
            barChart.getXAxis().setGranularityEnabled(true);
            barChart.setFitBars(true); // Ajustar las barras al gráfico
            barChart.invalidate(); // Refrescar el gráfico
        }
        private Map<String, Integer> contarGanadoPorRaza () {
            Map<String, Integer> ganadoPorRaza = new HashMap<>();
            for (Ganado ganado : listaGanado) {
                String raza = ganado.getRaza();
                ganadoPorRaza.put(raza, ganadoPorRaza.getOrDefault(raza, 0) + 1);
            }
            return ganadoPorRaza;
        }

    private void generarGraficoTemperatura() {
        // Obtener datos procesados
        List<Entry> entries = procesarDatosTemperatura();

        // Configurar el conjunto de datos para el gráfico de líneas
        LineDataSet dataSet = new LineDataSet(entries, "Temperatura Actual");
        dataSet.setValueTextSize(12f); // Tamaño del texto de los valores
        dataSet.setLineWidth(2f); // Grosor de la línea
        dataSet.setCircleRadius(8f); // Tamaño de los círculos en los puntos

        // Asignar colores personalizados a los puntos
        List<Integer> colores = getColoresTemperatura();
        dataSet.setCircleColors(colores); // Colores de los círculos

        // Crear el objeto LineData y asignarlo al gráfico
        LineData lineData = new LineData(dataSet);
        LineChart lineChart = findViewById(R.id.lineChart);
        lineChart.setData(lineData);

        // Personalizar el gráfico
        lineChart.getDescription().setEnabled(false); // Ocultar descripción
        lineChart.getXAxis().setGranularity(1f); // Espaciado en el eje X
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(getLabels())); // Etiquetas en el eje X
        lineChart.getAxisLeft().setAxisMinimum(25f);
        lineChart.getAxisLeft().setAxisMaximum(45f);// Valor mínimo en el eje Y
        lineChart.getAxisRight().setEnabled(false); // Deshabilitar el eje derecho

        // Refrescar el gráfico
        lineChart.invalidate();
    }
    private List<Entry> procesarDatosTemperatura() {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < listaTemperatura.size(); i++) {
            Temperatura temperatura = listaTemperatura.get(i);
            double tempActual = temperatura.getTemperaturaActual(); // Obtener la temperatura actual

            // Convertir el índice y la temperatura a float (requerido por Entry)
            float xValue = (float) i; // Eje X
            float yValue = (float) tempActual; // Eje Y

            // Agregar el punto al gráfico
            entries.add(new Entry(xValue, yValue));
        }
        return entries;
    }

    // Método para obtener los colores de los puntos según la temperatura
    private List<Integer> getColoresTemperatura() {
        List<Integer> colores = new ArrayList<>();
        for (Temperatura temperatura : listaTemperatura) {
            double tempActual = temperatura.getTemperaturaActual();
            if (tempActual > 39) {
                colores.add(ContextCompat.getColor(this, R.color.rojo)); // Rojo para > 39°C
            } else if (tempActual < 37) {
                colores.add(ContextCompat.getColor(this, R.color.celeste)); // Celeste para < 37°C
            } else {
                colores.add(ContextCompat.getColor(this, R.color.verde)); // Verde para valores entre 37 y 39°C
            }
        }
        return colores;
    }
    private List<String> getLabels() {
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < listaTemperatura.size(); i++) {
            labels.add("Sensor " + listaTemperatura.get(i).getIdSensor()); // Etiquetas personalizadas
        }
        return labels;
    }
}