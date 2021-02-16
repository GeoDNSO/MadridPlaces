package com.example.App.models.transfer;

import org.json.JSONException;
import org.json.JSONObject;

public class TUser implements JSONSerializable{
    private String username;
    private String password;
    private String name;
    private String surname;
    private String email;
    private String gender;
    private String birthDate;
    private String city;
    private String rol;

    public TUser(String username, String password, String name, String surname, String email, String gender, String birthDate, String city, String rol) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.gender = gender;
        this.birthDate = birthDate;
        this.city = city;
        this.rol = rol;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject infoJSON = new JSONObject();
        //Creando el JSON
        try {
            infoJSON.put("nickname", this.getUsername());
            infoJSON.put("name", this.getName());
            infoJSON.put("password", this.getPassword());
            infoJSON.put("surname", this.getSurname());
            infoJSON.put("email", this.getEmail());
            infoJSON.put("gender", this.getGender());
            infoJSON.put("birth_date", this.getBirthDate());
        } catch (JSONException e) {
            e.printStackTrace();
            infoJSON = null;
        }
        return infoJSON;
    }

    @Override
    public String jsonToString() {
        return this.toJSON().toString();
    }
}