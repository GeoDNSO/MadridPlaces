package com.example.App;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private NavController appNavController;
    private BottomNavigationView bottomNavView;
    private DrawerLayout drawerLayout;
    private NavigationView drawerNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Thread.sleep(300); //Para añadir un pequeño delay antes del splash screen
        setTheme(R.style.Theme_App);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set Bottom Navigation
        bottomNavView = findViewById(R.id.app_navigation);
        appNavController = Navigation.findNavController(this, R.id.app_host_fragment);


        //Drawer Navigation
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerNavigationView = findViewById(R.id.drawer_navigation_view);
        //drawerNavController = Navigation.findNavController(this, R.id.bottom_host_fragment);

        //Flecha arriba
        appBarConfiguration = new AppBarConfiguration.Builder(appNavController.getGraph())
                .setDrawerLayout(drawerLayout).build();


        //Navigation UI Setup
        NavigationUI.setupWithNavController(bottomNavView, appNavController);
        NavigationUI.setupWithNavController(drawerNavigationView, appNavController);
        NavigationUI.setupActionBarWithNavController(this, appNavController, drawerLayout);


    }

    //Para la flecha de arriba
    @Override
    public boolean onSupportNavigateUp() {
        /*
        NavController navController = Navigation.findNavController(this, R.id.bottom_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
        * */
        return NavigationUI.navigateUp(appNavController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}