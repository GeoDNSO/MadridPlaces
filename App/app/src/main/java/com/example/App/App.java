package com.example.App;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.App.models.dao.DAOUserImp;
import com.example.App.models.transfer.TUser;
import com.example.App.utilities.AppConstants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class App {

    private SessionManager sessionManager;

    private static App app;

    private Context context;
    private Menu menu;
    private BottomNavigationView bottomNavigationView;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private App(Context context) {
        this.context = context;
        sessionManager = new SessionManager(context);
    }

    public static App getInstance(Context ctx) {
        if (app == null)
            app = new App(ctx);
        app.context = ctx;
        app.sessionManager.setContext(ctx);
        return app;
    }

    public void logout() {
        sessionManager.logout();
        menu.findItem(R.id.profileFragment).setVisible(false);
        menu.findItem(R.id.adminFragment).setVisible(false);
    }

    public void setUserSession(TUser user) {
        sessionManager.setLogged(true);
        sessionManager.setUserInfo(user);

        //Opciones del menu

        menu.findItem(R.id.profileFragment).setVisible(true);
        if (user.getRol().equals(AppConstants.USER_ROL_ADMIN)) {
            menu.findItem(R.id.adminFragment).setVisible(true);
        }
    }

    public void menuOptions(boolean logged, boolean admin) {
        if (logged) {
            menu.findItem(R.id.profileFragment).setVisible(true);
        }
        if (admin) {
            menu.findItem(R.id.adminFragment).setVisible(true);
        }
    }



    public void askLocationPermission() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        addresses.get(0).getLatitude();
                        addresses.get(0).getLongitude();
                        addresses.get(0).getLocality();
                        addresses.get(0).getCountryCode();
                        addresses.get(0).getCountryName();
                        addresses.get(0).getAddressLine(0);

                        //Devolver address?? address contiene la infromacion de latitud y longitud
                    }
                }
            });

        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }

    public String getUsername(){
        return sessionManager.getUsername();
    }

    public boolean isLogged() {
        return sessionManager.isLogged();
    }

    public SessionManager getSessionManager() {
        return this.sessionManager;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public void setBottomNavigationView(BottomNavigationView menu){
        this.bottomNavigationView = menu;
    }

    public void setBottomMenuVisible(int val) {
        this.bottomNavigationView.setVisibility(val);
    }

    public boolean isAdmin() {
        return (sessionManager.getRol().equals(AppConstants.USER_ROL_ADMIN));
    }

}