package com.example.App;

import android.content.Context;

import com.example.App.transfer.TUser;

public class App {

    private SessionManager sessionManager;

    private static App app;

    private App(Context context){
        sessionManager = new SessionManager(context);
    }

    public static App getInstance(Context context){
        if(app == null)
            app = new App(context);

        app.sessionManager.setContext(context);
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
    }

    public void getUserData(){

    }

    public void getUsersList(){

    }

    public void setUserSession(TUser user){
        sessionManager.setLogged(true);
        sessionManager.setUserInfo(user);
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
}