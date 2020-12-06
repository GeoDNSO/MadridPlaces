package com.example.App;

import android.content.Context;
import android.view.Menu;

import com.example.App.transfer.TUser;

public class App {

    private SessionManager sessionManager;

    private static App app;
    private Context context;
    private Menu menu;

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

    public boolean registerUser(){
        return false;
    }

    public boolean loginUser(){
        return false;
    }

    public boolean modifyUser(){
        return false;
    }

    public boolean deleteUser(){
        return false;
    }

    public void logout(){
        sessionManager.logout();
        menu.findItem(R.id.profileFragment).setVisible(false);
        menu.findItem(R.id.adminFragment).setVisible(false);
    }

    public void getUserData(){

    }

    public void getUsersList(){

    }

    public void setUserSession(TUser user){
        sessionManager.setLogged(true);
        sessionManager.setUserInfo(user);

        //Opciones del menu

        menu.findItem(R.id.profileFragment).setVisible(true);
        if(user.isAdmin()){
            menu.findItem(R.id.adminFragment).setVisible(true);
        }
    }

    public String getUsername(){
        return sessionManager.getUsername();
    }

    public boolean isLogged() {
        return sessionManager.isLogged();
    }

    public boolean isAdmin() {
        return sessionManager.isAdmin();
    }

    public SessionManager getSessionManager() {
        return this.sessionManager;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}