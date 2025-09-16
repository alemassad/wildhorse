package com.example.crudejemplo.Controladora;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.crudejemplo.R;
import com.example.crudejemplo.Sesion.LoginActivity;
import com.google.android.material.navigation.NavigationView;

public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    String userRole;

    @Override
    public void setContentView(View view) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_base,null);
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);
        Toolbar toolbar = drawerLayout.findViewById(R.id.bar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = drawerLayout.findViewById(R.id.nav_view);

        //Intentamos de establecer los itenMenu por Roll
        Menu menu = navigationView.getMenu();

        SharedPreferences sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        userRole = sharedPreferences.getString("userRole", ""); // Obtén el userRole como String
        Log.e("Rol Drawer", userRole);
        switch (userRole) {
            case "ganadero":
                // Mostrar todos los items
                break;
            case "veterinario":
                // Ocultar items específicos para veterinarios
                menu.findItem(R.id.nav_permisos).setVisible(false);
                menu.findItem(R.id.nav_grupos).setVisible(false);
                menu.findItem(R.id.nav_personas).setVisible(false);
                break;
            case "peon":
                // Ocultar items específicos para peones
                menu.findItem(R.id.nav_permisos).setVisible(false);
                menu.findItem(R.id.nav_grupos).setVisible(false);
                menu.findItem(R.id.nav_personas).setVisible(false);
                menu.findItem(R.id.nav_vientres).setVisible(false);
                menu.findItem(R.id.nav_temperaturas).setVisible(false);
                break;
        }
        //fin item menu

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigation_drawer,R.string.close_navigation_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        manejaOpcionesNavegacion(item.getItemId());
        return false;
    }
    private void manejaOpcionesNavegacion(int itemId){
        if (itemId == R.id.nav_inicio){
            iniciarNuevaActividad(InicioActivity.class);
        }else if (itemId == R.id.nav_dispositivos) {
            iniciarNuevaActividad(DispositivoActivity.class);
        }else if (itemId == R.id.nav_ganados) {
            iniciarNuevaActividad(GanadoActivity.class);
        }else if (itemId == R.id.nav_grupos) {
            iniciarNuevaActividad(GrupoActivity.class);
        }else if (itemId == R.id.nav_permisos) {
            iniciarNuevaActividad(PermisoActivity.class);
        }else if (itemId == R.id.nav_personas) {
            iniciarNuevaActividad(UsuarioActivity.class);
        }else if (itemId == R.id.nav_razas) {
            iniciarNuevaActividad(RazaActivity.class);
        }else if (itemId == R.id.nav_temperaturas) {
            iniciarNuevaActividad(TemperaturaActivity.class);
        }else if (itemId == R.id.nav_vientres) {
            iniciarNuevaActividad(VientresActivity.class);
        }else if (itemId == R.id.nav_perfil) {
            iniciarNuevaActividad(LoginActivity.class);
        }else if (itemId == R.id.nav_dashboard) {
            iniciarNuevaActividad(DashboardActivity.class);
        }else if (itemId == R.id.nav_auditoria) {
            iniciarNuevaActividad(AuditoriaActivity.class);
        }
    }
    private void iniciarNuevaActividad(Class<?> destinoactividad){
        startActivity(new Intent(this, destinoactividad));
        overridePendingTransition(0,0);
    }
}