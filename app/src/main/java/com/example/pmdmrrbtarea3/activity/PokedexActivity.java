package com.example.pmdmrrbtarea3.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pmdmrrbtarea3.Language;
import com.example.pmdmrrbtarea3.R;
import com.example.pmdmrrbtarea3.adapter.ViewPagerAdapter;
import com.example.pmdmrrbtarea3.databinding.ActivityPokedexBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

public class PokedexActivity extends AppCompatActivity {

    private ActivityPokedexBinding binding;
    private FirebaseAuth mAuth;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Language.applySavedLocale(this);
        binding = ActivityPokedexBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setup();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu); // Inflar el menú
        return true;
    }

    // Manejar selección de opción en el menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Si el item seleccionado es "Acerca de..."
        if (id == R.id.action_about) {
            showAboutDialog(); // Mostrar el diálogo con la información de la app
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Mostrar el AlertDialog con la información de la app
    private void showAboutDialog() {
        // Crear el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.acerca_de) // Título del diálogo
                .setMessage(R.string.acercaDe) // Información de la app
                .setCancelable(true) // Permitirá cerrarlo tocando fuera del cuadro
                .setPositiveButton(R.string.cerrar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cerrar el diálogo cuando se haga clic en "Cerrar"
                        dialog.dismiss();
                    }
                });

        // Mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setup() {
        setTitle("POKEDEX");

        // Configuración de Toolbar
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        // Configuración TabLayout y ViewPager2
        tabLayout = binding.tabLayout;
        viewPager = binding.viewPager;

        // Configura el ViewPager con el adaptador
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Vincular TabLayout con ViewPager2 usando TabLayoutMediator
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText(getString(R.string.pokemon_capturados));
                            break;
                        case 1:
                            tab.setText(getString(R.string.pokemon_salvaje));
                            break;
                        case 2:
                            tab.setText(getString(R.string.ajustess));
                            break;
                    }
                }
        ).attach();

        // Configuración DrawerLayout y NavigationView
        drawerLayout = binding.drawerLayout;
        navigationView = binding.navigationView;

        // Configurar toggle para la hamburguesa
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Language.updateBaseContextLocale(newBase));
    }
}
