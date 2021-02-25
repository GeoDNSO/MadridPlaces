package com.example.App;

import android.content.Context;
import android.view.Menu;

import com.example.App.models.dao.DAOUserImp;
import com.example.App.models.transfer.TUser;
import com.example.App.utilities.AppConstants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class App {

    private SessionManager sessionManager;

    private static App app;
    private Context context;
    private Menu menu;
    private BottomNavigationView bottomNavigationView;

    private App(Context context){
        this.context = context;
        sessionManager = new SessionManager(context);
    }

    public static App getInstance(Context ctx){
        if(app == null)
            app = new App(ctx);
        app.context = ctx;
        app.sessionManager.setContext(ctx);
        return app;
    }

    public void logout(){
        sessionManager.logout();
        menu.findItem(R.id.profileFragment).setVisible(false);
        menu.findItem(R.id.adminFragment).setVisible(false);
    }

    public void setUserSession(TUser user){
        sessionManager.setLogged(true);
        sessionManager.setUserInfo(user);

        //Opciones del menu

        menu.findItem(R.id.profileFragment).setVisible(true);
        if(user.getRol().equals(AppConstants.USER_ROL_ADMIN)){
            menu.findItem(R.id.adminFragment).setVisible(true);
        }
    }

    public void menuOptions(boolean logged, boolean admin) {
        if(logged){
            menu.findItem(R.id.profileFragment).setVisible(true);
        }
        if(admin){
            menu.findItem(R.id.adminFragment).setVisible(true);
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