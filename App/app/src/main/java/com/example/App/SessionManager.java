package com.example.App;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.example.App.models.TUser;
import com.example.App.utilities.AppConstants;

import java.util.Locale;

public class SessionManager {

    private final String SHARED_PRIVATE_FILE = "pref";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    private SharedPreferences langPrefs;

    public SessionManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(SHARED_PRIVATE_FILE, Context.MODE_PRIVATE);
        this.editor = prefs.edit();

        this.langPrefs = context.getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = langPrefs.getString(AppConstants.APP_LANG, "es");
        setLocale(language);

    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.setLocale(locale);

        Resources resources = context.getResources();
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        SharedPreferences.Editor editor = context.getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
        editor.putString(AppConstants.APP_LANG, lang);
        editor.apply();
    }

    public void loadLocale(){
        String language = langPrefs.getString(AppConstants.APP_LANG, "es");
        setLocale(language);
    }

    public void setContext(Context context){
        this.context = context;
        this.prefs = context.getSharedPreferences(SHARED_PRIVATE_FILE, Context.MODE_PRIVATE);
        this.editor = prefs.edit();
    }

    public void setUsername(String username) {
        editor.putString(AppConstants.USERNAME, username).commit();
    }

    public String getUsername() {
        String username = prefs.getString(AppConstants.USERNAME,"");
        return username;
    }

    public void setLogged(boolean logged){
        editor.putBoolean(AppConstants.LOGGED, logged).commit();
    }

    public boolean isLogged(){
        return prefs.getBoolean(AppConstants.LOGGED, false);
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }

    public void setUserInfo(TUser user) {
        editor.putString(AppConstants.USERNAME, user.getUsername());
        editor.putString(AppConstants.NAME, user.getName());
        editor.putString(AppConstants.SURNAME, user.getSurname());
        editor.putString(AppConstants.CITY, user.getCity());
        editor.putString(AppConstants.PASSWORD, user.getPassword());
        editor.putString(AppConstants.EMAIL, user.getEmail());
        editor.putString(AppConstants.GENDER, user.getGender());
        editor.putString(AppConstants.BIRTH_DATE, user.getBirthDate());
        editor.putString(AppConstants.PROFILE_PICTURE, user.getImage_profile());
        editor.putString(AppConstants.ADMIN, user.getRol());
        editor.commit();
    }

    public String getEmail(){
        return prefs.getString(AppConstants.EMAIL, "");
    }

    public String getFirstName(){
        return prefs.getString(AppConstants.NAME, "");
    }

    public String getSurname(){
        return prefs.getString(AppConstants.SURNAME, "");
    }

    public String getPassword(){
        return prefs.getString(AppConstants.PASSWORD, "SIN_PETICION_A_PASS"); //Llamar a la BD
    }

    public String getRol(){
        return prefs.getString(AppConstants.ADMIN, "user");
    }

    public String getCity() {
        return prefs.getString(AppConstants.CITY, "");
    }

    public String getBirthDate() {
        return prefs.getString(AppConstants.BIRTH_DATE, "");
    }

    public String getGender() {
        return prefs.getString(AppConstants.GENDER, "");
    }

    public String getImageProfile() { return prefs.getString(AppConstants.PROFILE_PICTURE, ""); }

    public TUser getSessionUser(){
        return new TUser(getUsername(), getPassword(), getFirstName(),
                getSurname(), getEmail(), getGender(),
                getBirthDate(), getCity(), getRol(), getImageProfile());
    }

    public String getLangTag() {
        return  langPrefs.getString(AppConstants.APP_LANG, "es");
    }
}
