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

public class DAOUserImp implements DAOUser{

    //SI NECESITAIS PARAMETROS; CAMBIAD LA INTERFAZ
    volatile String responseRegister = null;
    volatile boolean controller = false;

    @Override
    public String registerObject(String nickname, String name, String password) {
        JSONObject dataLogin = new JSONObject();
        controller = false;
        try {
            //Creando el JSON
            dataLogin.put("nickname",nickname);
            dataLogin.put("name",name);
            dataLogin.put("password",password);
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
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            e.getMessage();
                            controller = true;
                            //Toast.makeText(MainActivity.this, "Algo no ha ido bien" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            call.cancel();

                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                responseRegister = response.body().string();
                                controller = true;
                                //Toast.makeText(MainActivity.this, response.body().string(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    });
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

    //@Override
    public TUser getObject() {
        return null;
    }

    //@Override
    public TUser deleteObject() {
        return null;
    }

    //@Override
    public TUser modifyObject() {
        return null;
    }

    //@Override
    public List<TUser> getListOfObjects() {
        return null;
    }
}