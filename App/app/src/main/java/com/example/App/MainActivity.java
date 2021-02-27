package com.example.App;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements MainActivityInterface{

    private AppBarConfiguration appBarConfiguration;
    private NavController appNavController;
    private BottomNavigationView bottomNavView;
    private DrawerLayout drawerLayout;
    private NavigationView drawerNavigationView;
    private NavigationView rightSideNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Thread.sleep(300); //Para añadir un pequeño delay antes del splash screen
        //setTheme(R.style.Theme_App);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();

        App app = App.getInstance(this);
        app.setMenu(drawerNavigationView.getMenu());
        app.setBottomNavigationView(bottomNavView);
    }


    public void initializeUI(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set Bottom Navigation
        bottomNavView = findViewById(R.id.app_navigation);
        appNavController = Navigation.findNavController(this, R.id.app_host_fragment);

        //Drawer Navigation
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerNavigationView = findViewById(R.id.drawer_navigation_view);
        //drawerNavController = Navigation.findNavController(this, R.id.bottom_host_fragment);

        //SideRight Navigation
        rightSideNavView = (NavigationView) findViewById(R.id.right_side_navigation_view);
        setDrawerLock();

        //Flecha de arriba
        appBarConfiguration = new AppBarConfiguration.Builder(appNavController.getGraph())
                .setDrawerLayout(drawerLayout).build(); //SetDrawerLayout(...) is deprecated but the app doens't work if we use other methods

        //Navigation UI Setup
        NavigationUI.setupWithNavController(bottomNavView, appNavController);
        NavigationUI.setupWithNavController(drawerNavigationView, appNavController);
        NavigationUI.setupWithNavController(rightSideNavView, appNavController);
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

    @Override
    public void setDrawerLock() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, rightSideNavView);
    }

    @Override
    public void setDrawerUnlock() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, rightSideNavView);
    }

    @Override
    public void openDrawer() {
        drawerLayout.openDrawer(rightSideNavView);
    }

    @Override
    public void closeDrawer() {
        drawerLayout.closeDrawer(rightSideNavView);
    }

    @Override
    public Menu sortUserNameMenuItem() {
        return rightSideNavView.getMenu();
    }

    @Override
    public Menu sortNameMenuItem() {
        return rightSideNavView.getMenu();
    }

}