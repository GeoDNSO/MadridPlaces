package com.example.App;

import android.content.Context;
import android.view.Menu;

import com.example.App.dao.DAOUserImp;
import com.example.App.transfer.TUser;
import com.example.App.utilities.AppConstants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;

import java.io.IOException;
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

    public boolean registerUser(String username, String pass, String name, String surname, String email, String gender, String birthDate, String city, String rol){
        DAOUserImp daoUser = new DAOUserImp();
        TUser user = new TUser(username, pass, name, surname, email, gender, birthDate, city, rol);

        boolean ok = false;

        try {
            ok = daoUser.registerObject(user);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ok;
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
        userList.add(new TUser("kqilsa", "dasdasdasd", "lsadnas", "asdasd","hola@gmail.com", "Hombre", "11/07/1992", "Madrid", "user"));

        return userList;
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