package com.example.App.dao;

import com.example.App.utilities.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SimpleRequest {


    private final String DIR_PROTOCOL = "http";
    private final String DIR = "10.0.2.2";
    private final int PORT = 5000;

    private volatile boolean finished;
    private volatile String response;

    public SimpleRequest(){
        finished = false;
        response = null;
    }


    public String getResponse(){
        return this.response;
    }

    public void createAndRunCall(Request request) {
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                finished = true;
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    finished = true;
                    throw new IOException("Unexpected code " + response);
                } else {
                    //HABIA UN TRY CATCH
                    SimpleRequest.this.response = response.body().string();
                    finished = true;
                }
            }
        });

        while (!finished) ;
    }


    public Request buildRequest(String postBodyString, String method, String route) {

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(postBodyString, mediaType);

        Request request = null;

        if(method.equals(AppConstants.METHOD_GET)){
            request = new Request.Builder()
                    .get()
                    .url(DIR_PROTOCOL + "://" + DIR + ":" + PORT + route)//Ej http://10.0.0.2:5000/login
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .build();
        }else{
            request = new Request.Builder()
                    .method(method, body)
                    .url(DIR_PROTOCOL + "://" + DIR + ":" + PORT + route)//Ej http://10.0.0.2:5000/login
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .build();
        }

        return request;
    }

    public boolean isSuccessful(String response) {
        if (response == null) {
            return false;
        }
        try {
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.get("exito").equals("true");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isSuccessful() {
        if (response == null) {
            return false;
        }
        try {
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.get("exito").equals("true");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}
