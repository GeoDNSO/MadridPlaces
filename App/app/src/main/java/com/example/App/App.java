package com.example.App;

import android.content.Context;

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

    public void setUsername(String username){
        sessionManager.setUsername(username);
    }

    public String getUsername(){
        return sessionManager.getUsername();
    }

}