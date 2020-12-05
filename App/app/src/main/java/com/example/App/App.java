package com.example.App;

public class App {

    private SessionManager sessionManager;

    public App(){

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