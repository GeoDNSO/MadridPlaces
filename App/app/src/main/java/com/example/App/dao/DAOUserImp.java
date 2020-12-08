package com.example.App.dao;

import android.widget.Toast;

import com.example.App.MainActivity;
import com.example.App.transfer.TUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DAOUserImp implements CRUD<TUser>, DAOUser{

    //SI NECESITAIS PARAMETROS; CAMBIAD LA INTERFAZ
    volatile String responseRegister = null;
    volatile String responseGetUser = null;
    volatile String responseLoginUser = null;
    volatile String responseModifyUser = null;
    volatile String responseDeleteUser = null;
    volatile String responseListUsers = null;
    volatile boolean controller = false;

    @Override
    public boolean registerObject(TUser u) {
        JSONObject dataLogin = new JSONObject();
        controller = false;
        responseRegister = null;
        try {
            //Creando el JSON
            dataLogin.put("nickname",u.getUsername());
            dataLogin.put("name",u.getName());
            dataLogin.put("password",u.getPassword());
            dataLogin.put("surname",u.getSurname());
            dataLogin.put("email",u.getEmail());
            dataLogin.put("gender",u.getGender());
            dataLogin.put("birth_date",u.getBirthDate());

            String json = dataLogin.toString();

            String postBodyString = json;
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(postBodyString, mediaType);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .post(body)
                    .url("http://" + "10.0.2.2" + ":" + 5000 + "/registration/")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .build();
            Call call = client.newCall(request);
            //call.timeout();
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    controller = true;
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {

                    if (!response.isSuccessful()) {
                        controller = true;
                        throw new IOException("Unexpected code " + response);
                    } else {
                        try {
                            responseRegister = response.body().string();
                            controller = true;
                        } catch (IOException e) {
                            controller = true;
                            e.printStackTrace();
                        }
                    }
                }
            });

            while(!controller);
            JSONObject response = new JSONObject(responseRegister);
            boolean success = response.get("exito").equals("true");
            return success;
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean login(String nickname, String password){
        JSONObject jsonUser = new JSONObject();
        controller = false;
        responseLoginUser = null;
        try{
            jsonUser.put("nickname", nickname);
            jsonUser.put("password", password);

            String postBodyString = jsonUser.toString();
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(postBodyString, mediaType);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .post(body)
                    .url("http://" + "10.0.2.2" + ":" + 5000 + "/login/")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure( Call call,  IOException e) {
                    e.printStackTrace();
                    controller = true;
                    call.cancel();
                }

                @Override
                public void onResponse( Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        controller = true;
                        throw new IOException("Unexpected code " + response);
                    } else {
                        try {
                            responseLoginUser = response.body().string();
                            controller = true;
                        } catch (IOException e) {
                            controller = true;
                            e.printStackTrace();
                        }
                    }
                }
            });
            while(!controller);
            if(responseLoginUser != null){
                JSONObject response = new JSONObject(responseLoginUser);
                return response.get("exito").equals("true");
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return false;
    }
    public TUser getUser(String nickname) {
        JSONObject jsonUser = new JSONObject();
        controller = false;
        responseGetUser = null;
        try {
            jsonUser.put("nickname", nickname);
            String postBodyString = jsonUser.toString();
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(postBodyString, mediaType);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .post(body)
                    .url("http://" + "10.0.2.2" + ":" + 5000 + "/profileUser/")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                    controller = true;
                    call.cancel();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        controller = true;
                        throw new IOException("Unexpected code " + response);
                    } else {
                        try {
                            responseGetUser = response.body().string();
                            controller = true;
                        } catch (IOException e) {
                            controller = true;
                            e.printStackTrace();
                        }
                    }
                }
            });

            while(!controller);
            if(responseGetUser != null) {
                JSONObject response = new JSONObject(responseGetUser);
                return new TUser(response.getString("nickname"), response.getString("password")/*antes estaba con ""*/, response.getString("name"), response.getString("surname"),response.getString("email"), response.getString("gender"), response.getString("birth_date"),response.getString("city") , response.getString("rol"));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public TUser getObject() {
        return null;
    }

    @Override
    public boolean deleteObject(String nickname) {
        JSONObject jsonUser = new JSONObject();
        controller = false;
        responseDeleteUser = null;
        try{
            jsonUser.put("nickname", nickname);

            String postBodyString = jsonUser.toString();
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(postBodyString, mediaType);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .delete(body)
                    .url("http://" + "10.0.2.2" + ":" + 5000 + "/deleteUser/")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure( Call call,  IOException e) {
                    e.printStackTrace();
                    controller = true;
                    call.cancel();
                }

                @Override
                public void onResponse( Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        controller = true;
                        throw new IOException("Unexpected code " + response);
                    } else {
                        try {
                            responseDeleteUser = response.body().string();
                            controller = true;
                        } catch (IOException e) {
                            controller = true;
                            e.printStackTrace();
                        }
                    }
                }
            });
            while(!controller);
            if(responseDeleteUser != null){
                JSONObject response = new JSONObject(responseDeleteUser);
                return response.get("exito").equals("true");
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean modifyObject(TUser u) {
        JSONObject dataLogin = new JSONObject();
        controller = false;
        responseModifyUser = null;
        try {
            //Creando el JSON
            dataLogin.put("nickname",u.getUsername());
            dataLogin.put("name",u.getName());
            dataLogin.put("password",u.getPassword());
            dataLogin.put("surname",u.getSurname());
            dataLogin.put("email",u.getEmail());
            dataLogin.put("gender",u.getGender());
            dataLogin.put("birth_date",u.getBirthDate());

            String json = dataLogin.toString();

            String postBodyString = json;
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(postBodyString, mediaType);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .put(body)
                    .url("http://" + "10.0.2.2" + ":" + 5000 + "/modifyUser/")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .build();
            Call call = client.newCall(request);
            //call.timeout();
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    controller = true;
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {

                    if (!response.isSuccessful()) {
                        controller = true;
                        throw new IOException("Unexpected code " + response);
                    } else {
                        try {
                            responseModifyUser = response.body().string();
                            controller = true;
                        } catch (IOException e) {
                            controller = true;
                            e.printStackTrace();
                        }
                    }
                }
            });

            while(!controller);
            JSONObject response = new JSONObject(responseModifyUser);
            boolean success = response.get("exito").equals("true");
            return success;
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<TUser> getListOfObjects() {
        controller = false;
        responseListUsers = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .get() //No recibe nada en el body, asi que mejor get en vez de post
                    .url("http://" + "10.0.2.2" + ":" + 5000 + "/listUsers/")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                    controller = true;
                    call.cancel();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        controller = true;
                        throw new IOException("Unexpected code " + response);
                    } else {
                        try {
                            responseListUsers = response.body().string();
                            controller = true;
                        } catch (IOException e) {
                            controller = true;
                            e.printStackTrace();
                        }
                    }
                }
            });
            while(!controller);
            if(responseListUsers != null) {
                JSONObject response = new JSONObject(responseListUsers);
                List<TUser> listUsers = new ArrayList<TUser>(); //dentro de get("users") contiene una lista de nicknames ["poti", "aaa", "pepe"]
                List<String> reponseNicknames = (List<String>) response.get("users");  //get() recoge un objeto generico, no se si se puede haacer casting a un array de String
                for (String user:reponseNicknames) {
                    TUser tUser = getUser(user);
                    listUsers.add(tUser);
                }
                return listUsers;
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return null;

}