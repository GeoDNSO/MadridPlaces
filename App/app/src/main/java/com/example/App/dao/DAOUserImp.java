package com.example.App.dao;

import com.example.App.transfer.TUser;
import com.example.App.utilities.AppConstants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DAOUserImp implements CRUD<TUser>, DAOUser {

    @Override
    public boolean registerObject(TUser u) {

        String postBodyString = u.jsonToString();

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/registration/");

        simpleRequest.createAndRunCall(request);

        String response = simpleRequest.getResponse();

        return simpleRequest.isSuccessful(response);
    }

    public boolean login(String nickname, String password) {

        String postBodyString = loginInfoToString(nickname, password);

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/login/");

        simpleRequest.createAndRunCall(request);

        String response = simpleRequest.getResponse();

        return simpleRequest.isSuccessful(response);
    }

    public TUser getUser(String nickname) {

        JSONObject jsonUser = new JSONObject();

        try {
            jsonUser.put("nickname", nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String postBodyString = jsonUser.toString();

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/profileUser/");

        simpleRequest.createAndRunCall(request);

        String response = simpleRequest.getResponse();

        if(response == null) {
            return null;
        }

        return jsonStringToUser(response);
    }

    @Override
    public TUser getObject() {
        return null;
    }

    @Override
    public boolean deleteObject(String nickname) {

        JSONObject jsonUser = new JSONObject();

        try {
            jsonUser.put("nickname", nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String postBodyString = jsonUser.toString();

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_DELETE, "/deleteUser/");

        simpleRequest.createAndRunCall(request);

        String response = simpleRequest.getResponse();

        return simpleRequest.isSuccessful(response);
    }

    @Override
    public boolean modifyObject(TUser u) {
        String postBodyString = u.jsonToString();

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_PUT, "/modifyUser/");

        simpleRequest.createAndRunCall(request);

        String response = simpleRequest.getResponse();

        return simpleRequest.isSuccessful(response);
    }

    @Override
    public List<TUser> getListOfObjects() {

       SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest("",
                AppConstants.METHOD_GET, "/listUsers/");

        simpleRequest.createAndRunCall(request);

        String response = simpleRequest.getResponse();

        if(response == null){
            return null;
        }
        return  getListFromResponse(response);
    }


    //Metodos privados para la gestion de los JSON de este DAO

    private String loginInfoToString(String nickname, String password){
        JSONObject jsonUser = new JSONObject();
        String infoString;
        try {
            jsonUser.put("nickname", nickname);
            jsonUser.put("password", password);
        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = jsonUser.toString();

        return infoString;
    }

    private TUser jsonStringToUser(String jsonString){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            return new TUser(jsonObject.getString("nickname"), jsonObject.getString("password")/*antes estaba con ""*/, jsonObject.getString("name"), jsonObject.getString("surname"), jsonObject.getString("email"), jsonObject.getString("gender"), jsonObject.getString("birth_date"), jsonObject.getString("city"), jsonObject.getString("rol"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<TUser> getListFromResponse(String response){
        JSONObject jresponse = null;
        try {
            jresponse = new JSONObject(response);

            //dentro de get("users") contiene una lista de nicknames ["poti", "aaa", "pepe"]
            List<TUser> listUsers = new ArrayList<TUser>();
            JSONArray arrayNicknames = jresponse.getJSONArray("users");
            for (int i = 0; i < arrayNicknames.length(); i++) {
                TUser tUser = getUser(arrayNicknames.getString(i));
                listUsers.add(tUser);
            }
            return listUsers;
        } catch (JSONException e) {
            e.printStackTrace();
            return  null;
        }
    }
}