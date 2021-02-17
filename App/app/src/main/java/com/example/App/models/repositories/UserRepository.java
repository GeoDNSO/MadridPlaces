package com.example.App.models.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.App.models.dao.SimpleRequest;
import com.example.App.models.transfer.TUser;
import com.example.App.utilities.AppConstants;

import org.jetbrains.annotations.NotNull;
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

public class UserRepository {



    private MutableLiveData<Boolean> mSuccess = new MutableLiveData<>();

    private MutableLiveData<TUser> mUser = new MutableLiveData<>();

    public void registerUser(TUser user) {

        String postBodyString = user.jsonToString();
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(
                postBodyString,
                AppConstants.METHOD_POST, "/registration/"
        );
        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mSuccess.postValue(false);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                mSuccess.postValue(simpleRequest.isSuccessful(response));

            }
        });
    }

    public void loginUser(String nickname, String password) {

        String postBodyString = loginInfoToString(nickname, password);

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/login/");

        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mSuccess.postValue(false);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if (success){
                    mUser.postValue(jsonStringToUser(res));
                }
                else{
                    mUser.postValue(null);
                }
                mSuccess.postValue(success);//Importante que este despues del postValue de mUser
            }
        });

    }

    public void getUser(String nickname) {

        JSONObject jsonUser = new JSONObject();
        try {
            jsonUser.put("nickname", nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String postBodyString = jsonUser.toString();

        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(
                postBodyString,
                AppConstants.METHOD_POST, "/registration/"
        );

        Call call = simpleRequest.createCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mSuccess.postValue(false);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    mSuccess.postValue(false);
                    throw new IOException("Unexpected code " + response);
                }
                mSuccess.postValue(simpleRequest.isSuccessful(response));
                TUser user = jsonStringToUser(response.body().string());
                mUser.postValue(user);
            }
        });
    }

    public void deleteUser(String nickname){
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

        Call call = simpleRequest.createCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mSuccess.postValue(false);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mSuccess.postValue(false);
                    throw new IOException("Unexpected code " + response);
                }
                mSuccess.postValue(simpleRequest.isSuccessful(response));
            }
        });
    }

    public void modifyUser(TUser u) {
        String postBodyString = u.jsonToString();

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_PUT, "/modifyUser/");

        Call call = simpleRequest.createCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mSuccess.postValue(false);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mSuccess.postValue(false);
                    throw new IOException("Unexpected code " + response);
                }
                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if (success){
                    mUser.postValue(jsonStringToUser(res));
                }
                else{
                    mUser.postValue(null);
                }
                mSuccess.postValue(success);//Importante que este despues del postValue de mUser
            }
        });

    }



    //Getter y Setters para LiveData

    public MutableLiveData<Boolean> getmSuccess() {
        return mSuccess;
    }

    public void setmSuccess(MutableLiveData<Boolean> mSuccess) {
        this.mSuccess = mSuccess;
    }

    public MutableLiveData<TUser> getmUser() {
        return mUser;
    }

    public void setmUser(MutableLiveData<TUser> mUser) {
        this.mUser = mUser;
    }

    //Métodos Privados para JSONs

    //Crea un JSON con la información necesaria para el login: nickname y password
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

    //Convierte String con formato json en usuario
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


}