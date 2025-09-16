package com.example.crudejemplo.Controladora;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.crudejemplo.databinding.ActivityInicioBinding;
import com.example.crudejemplo.Mapa.GeofenceHelper;
import com.example.crudejemplo.R;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.List;

public class InicioActivity extends DrawerBaseActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener {

    private static final String TAG = "InicioActivity";
    private static final int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;
    private static float GEOFENCE_RADIUS = 150;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    ActivityInicioBinding activityInicioBinding;
    private GoogleMap mMap;
    private GeofenceHelper geofenceHelper;
    private GeofencingClient geofencingClient;
    private EditText txtLatitud, txtLongitud;
    private boolean isFirstLoad = true; // Variable para rastrear la primera carga

    // ... Para dimencionar el geovallado cuadrado ...
    double latitud = -32.9440077;// Latitud del centro del geovallado
    double longitud = -60.6629943; // Longitud del centro del geovallado
    double lado = 550.0; // Longitud del lado del cuadrado en metros

    private FusedLocationProviderClient fusedLocationClient;// fusedLocation
    DatabaseReference mReference; //Database reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityInicioBinding = ActivityInicioBinding.inflate(getLayoutInflater());
        setContentView(activityInicioBinding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        txtLatitud = findViewById(R.id.textLatitud);
        txtLongitud = findViewById(R.id.textLongitud);
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

        // fusedLocation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Database reference
        mReference = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng rosario = new LatLng(-32.9482432, -60.6625402);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rosario, 15));

        Polygon geovallado = agregarGeovalladoCuadrado(mMap, latitud, longitud, lado);
        cargarMarcadoresDesdeRealtimeDatabase(mMap, geovallado);
        enableUserLocation();
        this.mMap.setOnMapClickListener(this);
        this.mMap.setOnMapLongClickListener(this);
    }

    public Polygon agregarGeovalladoCuadrado(GoogleMap mMap, double latitud, double longitud, double lado) {
        // Coordenadas de las esquinas del cuadrado
        double mediaDistancia = lado / 2.0; // La mitad de la longitud del lado del cuadrado
        LatLng esquina1 = SphericalUtil.computeOffset(new LatLng(latitud, longitud), mediaDistancia, 45.0); // Esquina superior derecha
        LatLng esquina2 = SphericalUtil.computeOffset(new LatLng(latitud, longitud), mediaDistancia, 135.0); // Esquina superior izquierda
        LatLng esquina3 = SphericalUtil.computeOffset(new LatLng(latitud, longitud), mediaDistancia, 225.0); // Esquina inferior izquierda
        LatLng esquina4 = SphericalUtil.computeOffset(new LatLng(latitud, longitud), mediaDistancia, 315.0); // Esquina inferior derecha

        // Polígono con las esquinas del cuadrado
        Polygon geovallado = mMap.addPolygon(new PolygonOptions()
                .add(esquina1, esquina2, esquina3, esquina4)
                .strokeColor(Color.GREEN)
                .fillColor(Color.argb(70, 0, 255, 0)) // Verde transparente
        );
        geovallado.setVisible(true);
        LatLng latLng = new LatLng(latitud,longitud);
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, GEOFENCE_RADIUS,
                Geofence.GEOFENCE_TRANSITION_DWELL |
                        Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.geofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Exito: "+"Geofence agregado");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "En falla: "+error);
                    }
                });
        return geovallado;
    }

    public void cargarMarcadoresDesdeRealtimeDatabase(GoogleMap mMap, Polygon geovallado) {
        BitmapDescriptor iconoCabaIn = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);//Color del marcador
        BitmapDescriptor iconoCabaOut = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = db.getReference().child("LatLng");

        // Verifica permisos antes de obtener la ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
            return;
        }

        // Obtiene la ubicación actual
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());

                // Solo mueve la cámara y ajusta el zoom si es la primera vez
                if (isFirstLoad) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 15));
                    isFirstLoad = false; // Marca como no primera carga
                }
            } else {
                Log.e("MapaActivity", "No se pudo obtener la ubicación actual");
            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ubicacionSnapshot : snapshot.getChildren()) {
                    Double latitud = ubicacionSnapshot.child("Latitud").getValue(Double.class);
                    Double longitud = ubicacionSnapshot.child("Longitud").getValue(Double.class);

                    if (latitud != null && longitud != null) {
                        LatLng ubicacion = new LatLng(latitud, longitud);
                        boolean dentro = isPointInPolygon(ubicacion, geovallado);

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(ubicacion)
                                .title(ubicacionSnapshot.getKey())
                                .icon(dentro ? iconoCabaIn : iconoCabaOut);
                        mMap.addMarker(markerOptions);
                    } else {
                        Log.e("MapaActivity", "Datos nulos en la base de datos");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MapaActivity", "Error al leer la base de datos: " + error.getMessage());
            }
        });
    }
    //Metodo para habilitar locacion del usuario
    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //preguntamos por el permiso
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar una explicación al usuario si es necesario
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                // El usuario aceptó el permiso
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                // Manejar el caso en que el usuario denegó el permiso
                Toast.makeText(this, "Es necesario que le des permiso de Locacion Fina para las Geovallas... ", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Ya puedes agregar geofences", Toast.LENGTH_SHORT).show();
            } else {
                // Manejar el caso en que el usuario denegó el permiso
                Toast.makeText(this, "Es necesario el permiso de BackGround para agregar geofences", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

        if (Build.VERSION.SDK_INT >= 29){
            //se necesitan permisos de BackGround
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(latLng);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //Pedimos el permiso
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

        } else {
            handleMapLongClick(latLng);
        }
    }


   private void handleMapLongClick(LatLng latLng) {
        // Registrar la acción en la auditoría
        AuditoriaUtil.registrarAccion("Agrega Geovallado");

        mMap.clear();
        Polygon poligono = agregarGeovalladoCuadrado(mMap, latLng.latitude, latLng.longitude, lado);
        agregarGeovalladoCuadrado(mMap, latLng.latitude, latLng.longitude, lado);
        cargarMarcadoresDesdeRealtimeDatabase(mMap, poligono);

    }
    @Override
    public void onMapClick(@NonNull LatLng latLng) {

        // Registrar la acción en la auditoría
        AuditoriaUtil.registrarAccion("click en el mapa: "+latLng.latitude+","+latLng.longitude);

        txtLatitud.setText(" " + latLng.latitude);
        txtLongitud.setText(" " + latLng.longitude);

    }
    private boolean isPointInPolygon(LatLng point, Polygon polygon) {
        int intersectCount = 0;
        List<LatLng> vertices = polygon.getPoints();

        for (int i = 0; i < vertices.size() - 1; i++) {
            LatLng v1 = vertices.get(i);
            LatLng v2 = vertices.get(i + 1);

            if ((v1.longitude > point.longitude) != (v2.longitude > point.longitude)) {
                double slope = (v2.latitude - v1.latitude) / (v2.longitude - v1.longitude);
                double intersectLat = v1.latitude + slope * (point.longitude - v1.longitude);
                if (point.latitude < intersectLat) {
                    intersectCount++;
                }
            }
        }
        return (intersectCount % 2) == 1;
    }

}