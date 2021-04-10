package com.example.App;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.App.utilities.AppConstants;
import com.example.App.utilities.PermissionsManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.mapbox.mapboxsdk.Mapbox;

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
        setTheme(R.style.Theme_App);
        super.onCreate(savedInstanceState);
        // Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);

        initializeUI();

        setGlobalVariables();

        App app = App.getInstance(this);
        app.setMenu(drawerNavigationView.getMenu());
        app.setBottomNavigationView(bottomNavView);
        app.setMainActivity(this);
        app.askLocationPermission();

    }


    private void setGlobalVariables() {
        AppConstants.TAB_RATING = getString(R.string.tab_rating);
        AppConstants.TAB_NEAREST = getString(R.string.tab_nearest);
        AppConstants.TAB_TWITTER = getString(R.string.tab_twitter);
        AppConstants.TAB_CATEGORY = getString(R.string.tab_category);
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
        inflateMenu();

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


        appNavControllerDestinationListener();
    }

    //Listener para controlar en que vista se verá el menú inferior de la aplicación
    private void appNavControllerDestinationListener() {
        appNavController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                int dest_id = destination.getId();
                if(dest_id == R.id.loginFragment || dest_id == R.id.registerFragment ||
                        dest_id == R.id.profileFragment || dest_id == R.id.adminFragment ||
                        dest_id == R.id.detailFragment || dest_id == R.id.addPlaceFragment || dest_id == R.id.modifyPlaceFragment || dest_id == R.id.mapFragment){
                    bottomNavView.setVisibility(View.GONE);
                }
                else{
                    bottomNavView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        App app = App.getInstance(this);
        drawerNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logOut:
                        app.logout();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        drawerNavigationView.refreshDrawableState();
                        inflateMenu();

                        return true;
                    default:
                        inflateMenu();
                        NavigationUI.onNavDestinationSelected(item, appNavController);
                        drawerLayout.closeDrawer(GravityCompat.START);

                        return true;
                }
            }
        });
        return true;
    }

    //Construye el menu del drawerLayout según la sesión del usuario
    public void inflateMenu(){
        App app = App.getInstance(this);
        if(app.isLogged()){
            drawerNavigationView.getMenu().clear();
            drawerNavigationView.inflateMenu(R.menu.drawer_login_navigation_menu);
        } else
        {
            drawerNavigationView.getMenu().clear();
            drawerNavigationView.inflateMenu(R.menu.drawer_logout_navigation_menu);
        }
        app.setMenu(drawerNavigationView.getMenu());
    }


    //Para la flecha de arriba
    @Override
    public boolean onSupportNavigateUp() {
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

    //@TODO activar y desactivar el servicio de tracking
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i("SET", "LLEGA A ONREQUEST");
        switch (requestCode) {
            case PermissionsManager
                    .GEOLOCATION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    Log.i("SET", "Permiso garantizado");

                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Log.i("SET", "SIN PERMISO");
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

}