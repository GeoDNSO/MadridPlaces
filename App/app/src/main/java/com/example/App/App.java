package com.example.App;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.App.networking.SimpleRequest;
import com.example.App.models.TUser;
import com.example.App.services.LocationTrack;
import com.example.App.components.LogoutObserver;
import com.example.App.utilities.AppConstants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class App {

    private SessionManager sessionManager;

    private static App app;
    private List<LogoutObserver> logoutObservers;
    private Context context;
    private Menu menu;
    private BottomNavigationView bottomNavigationView;
    private NavigationView drawerNavigationView;

    private MainActivity mainActivity;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationTrack locationTrack;

    private App(Context context) {
        this.context = context;
        sessionManager = new SessionManager(context);
        logoutObservers = new ArrayList<>();
    }

    public static App getInstance(Context ctx) {
        if (app == null)
            app = new App(ctx);
        app.context = ctx;
        app.sessionManager.setContext(ctx);
        return app;
    }

    public static App getInstance() {
        if (app == null)
            return null;
        return app;
    }

    public void logout() {
        sessionManager.logout();
        for (int i = 0; i < logoutObservers.size(); ++i){
            logoutObservers.get(i).onLogout();
        }
        Toast.makeText(getMainActivity(), "Se ha cerrado la sesión", Toast.LENGTH_SHORT).show();
    }

    public void addLogoutObserver(LogoutObserver logoutObserver){
        logoutObservers.add(logoutObserver);
    }

    public void setUserSession(TUser user) {
        sessionManager.setLogged(true);
        sessionManager.setUserInfo(user);
        //Opciones del menu

        mainActivity.inflateMenu();

        if (user.getRol().equals(AppConstants.USER_ROL_ADMIN)) {
            MenuItem item = menu.findItem(R.id.adminFragment);
            if(item != null) {
                item.setVisible(true);
            }
        }
    }

    public void menuOptions(boolean logged, boolean admin) {
        if (logged) {
            //drawerNavigationView.getMenu().clear();
            //drawerNavigationView.inflateMenu(R.menu.drawer_login_navigation_menu);
            if (admin) {
                MenuItem item = menu.findItem(R.id.adminFragment);
                if(item != null) {
                    item.setVisible(true);
                }
            }
        } else
        {
            //drawerNavigationView.getMenu().clear();
            //drawerNavigationView.inflateMenu(R.menu.drawer_logout_navigation_menu);
        }

    }



    public void askLocationPermission() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //TODO Borrar Lo de Abajo
            /*

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
            */
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

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    public static boolean isServerReachable(){
        Callable<Boolean> task = ()->{
            return SimpleRequest.isHostReachable();
        };

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<Boolean> future = executorService.submit(task);

        boolean reachable = false;
        try {
            reachable = future.get().booleanValue();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return reachable;
    }


    public String getAppString(int id){
        return this.mainActivity.getString(id);
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setLocationTrack(LocationTrack locationTrack) {
        this.locationTrack = locationTrack;
    }

    public TUser getSessionUser(){
        return sessionManager.getSesionUser();
    }

    public LocationTrack getLocationTrack() {
        return locationTrack;
    }
}