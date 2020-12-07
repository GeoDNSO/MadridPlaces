package com.example.App.dao;

import android.widget.Toast;

import com.example.App.MainActivity;
import com.example.App.transfer.TUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DAOUserImp implements /*CRUD<TUser>,*/ DAOUser{

    //SI NECESITAIS PARAMETROS; CAMBIAD LA INTERFAZ
    volatile String responseRegister = null;
    volatile boolean controller = false;


    //@Override
    public String registerObject(TUser object) {

        TUser u = new TUser("JMorales", "xxxx","Juan", "Morales",
                "juan@gmail.com", "H", "01/01/1990",
                "Madrid", true);
        JSONObject dataLogin = new JSONObject();
        controller = false;
        try {
            //Creando el JSON
            dataLogin.put("nickname",u.getUsername());
            dataLogin.put("name",u.getName());
            dataLogin.put("password",u.getPassword());
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
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    controller = true;
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {

                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    } else {
                        try {
                            responseRegister = response.body().string();
                            controller = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            while(!controller);

            return responseRegister;
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return null;
    }


}