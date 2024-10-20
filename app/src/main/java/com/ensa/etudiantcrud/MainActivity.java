package com.ensa.etudiantcrud;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;  // Import MenuItem to avoid errors
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView; // Import the correct SearchView
import com.ensa.etudiantcrud.ui.AddEtudiantActivity;
import com.ensa.etudiantcrud.ui.adapter.EtudiantAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.ensa.etudiantcrud.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private EtudiantAdapter etudiantAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEtudiantActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Charger le menu
        getMenuInflater().inflate(R.menu.main, menu);

        // Trouver l'élément de partage
        MenuItem shareItem = menu.findItem(R.id.action_settings);


        shareItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                // Appeler la méthode de partage
                shareApp();
                return true;
            }
        });

        return true;
    }

    // Méthode pour partager l'application
    private void shareApp() {
        // Corps du message à partager
        String shareBody = "Découvrez cette application incroyable ! " +
                "https://play.google.com/store/apps/details?id=" + getPackageName();

        // Créer une Intent de partage
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Partager l'application");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

        // Lancer le sélecteur d'applications pour partager
        startActivity(Intent.createChooser(shareIntent, "Partager via"));
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
