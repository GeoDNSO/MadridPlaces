package com.example.App;

import android.content.Context;
import android.view.Menu;

import com.example.App.transfer.TUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class App {

    private SessionManager sessionManager;

    private static App app;
    private Context context;
    private Menu menu;
    private BottomNavigationView bottomNavigationView;
    private List<TUser> userList;

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

    public List<TUser> getUsersList(){
        userList = new ArrayList<>();
        userList.add(new TUser("kqilsa", "dasdasdasd", "lsadnas", "asdasd","hola@gmail.com", "Hombre", "11/07/1992", "Madrid", false));
        userList.add(new TUser("asdasd", "dasdasdasd", "lsadnas", "asdasd","hola@gmail.com", "Hombre", "11/07/1992", "Madrid", false));
        userList.add(new TUser("kqisdfslsa", "dasdasdasd", "lsadnas", "asdasd","hola@gmail.com", "Hombre", "11/07/1992", "Madrid", false));
        userList.add(new TUser("kqisdflsa", "dasdasdasd", "lsadnas", "asdasd","hola@gmail.com", "Hombre", "11/07/1992", "Madrid", false));
        userList.add(new TUser("kqiasdalsa", "dasdasdasd", "lsadnas", "asdasd","hola@gmail.com", "Hombre", "11/07/1992", "Madrid", false));
        userList.add(new TUser("kqiasdalsa", "dasdasdasd", "lsadnas", "asdasd","hola@gmail.com", "Hombre", "11/07/1992", "Madrid", false));
        userList.add(new TUser("kqiassdasdalsa", "dasdasdasd", "lsadnas", "asdasd","hola@gmail.com", "Hombre", "11/07/1992", "Madrid", false));
        userList.add(new TUser("kqiasdaassdalsa", "dasdasdasd", "lsadnas", "asdasd","hola@gmail.com", "Hombre", "11/07/1992", "Madrid", false));
        userList.add(new TUser("kqiasdalasdassa", "dasdasdasd", "lsadnas", "asdasd","hola@gmail.com", "Hombre", "11/07/1992", "Madrid", false));
        userList.add(new TUser("kqiasdaasdasdlsa", "dasdasdasd", "lsadnas", "asdasd","hola@gmail.com", "Hombre", "11/07/1992", "Madrid", false));
        userList.add(new TUser("kqiasasddalsa", "dasdasdasd", "lsadnas", "asdasd","hola@gmail.com", "Hombre", "11/07/1992", "Madrid", false));
        userList.add(new TUser("kqiasdasdalsa", "dasdasdasd", "lsadnas", "asdasd","hola@gmail.com", "Hombre", "11/07/1992", "Madrid", false));
        userList.add(new TUser("kqiasasdasdalsa", "dasdasdasd", "lsadnas", "asdasd","hola@gmail.com", "Hombre", "11/07/1992", "Madrid", false));
        userList.add(new TUser("kqiasasdadalsa", "dasdasdasd", "lsadnas", "asdasd","hola@gmail.com", "Hombre", "11/07/1992", "Madrid", false));

        return userList;
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

    public boolean isAdmin() {
        return sessionManager.isAdmin();
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
}