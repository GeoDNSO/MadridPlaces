package com.example.App.dao;

import android.widget.Toast;

import com.example.App.MainActivity;
import com.example.App.transfer.TUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    volatile boolean controller = false;

    @Override
    public boolean registerObject(TUser u) {
        JSONObject dataLogin = new JSONObject();
        controller = false;
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

    @Override
    public TUser getObject() {
        return null;
    }

    @Override
    public boolean deleteObject(TUser object) {
        return false;
    }

    @Override
    public boolean modifyObject(TUser object) {
        return false;
    }

    @Override
    public List<TUser> getListOfObjects() {
        return null;
    }


}