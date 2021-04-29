package com.example.App.models.repositories;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MutableLiveData;

import com.example.App.models.dao.SimpleRequest;
import com.example.App.models.repositories.helpers.UserRespositoryHelper;
import com.example.App.models.transfer.TPlace;
import com.example.App.models.transfer.TUser;
import com.example.App.utilities.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class UserRepository {

    private MutableLiveData<Boolean> mSuccess = new MutableLiveData<>();
    private MutableLiveData<Integer> mProfileSuccess = new MutableLiveData<>();

    private MutableLiveData<TUser> mUser = new MutableLiveData<>();
    private MutableLiveData<List<TUser>> mListUsers = new MutableLiveData<>();

    private MutableLiveData<Pair<Integer,Integer>> mCountProfileCommentsAndHistory = new MutableLiveData<>();

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

        String postBodyString = UserRespositoryHelper.loginInfoToString(nickname, password);

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
                    mUser.postValue(UserRespositoryHelper.jsonStringToUser(res));
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
                TUser user = UserRespositoryHelper.jsonStringToUser(response.body().string());
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
                mProfileSuccess.postValue(AppConstants.ERROR_PROFILE);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mProfileSuccess.postValue(AppConstants.ERROR_PROFILE);
                    throw new IOException("Unexpected code " + response);
                }
                Boolean success = simpleRequest.isSuccessful(response);
                if(success){
                    mProfileSuccess.postValue(AppConstants.DELETE_PROFILE);
                }
                else{
                    mProfileSuccess.postValue(AppConstants.ERROR_PROFILE);
                }

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
                mProfileSuccess.postValue(AppConstants.ERROR_PROFILE);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mProfileSuccess.postValue(AppConstants.ERROR_PROFILE);
                    throw new IOException("Unexpected code " + response);
                }
                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if (success){
                    mUser.postValue(UserRespositoryHelper.jsonStringToUser(res));
                    Log.d("Caca", res);
                    mProfileSuccess.postValue(AppConstants.MODIFY_PROFILE);//Importante que este despues del postValue de mUser
                }
                else{
                    mUser.postValue(null);
                    mProfileSuccess.postValue(AppConstants.ERROR_PROFILE);//Importante que este despues del postValue de mUser
                }
            }
        });

    }

    public void listUsers(int page, int quantum, String searchText, int sortType) {
        String postBodyString = UserRespositoryHelper.paramsToString(page, quantum, searchText, sortType);

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/listUsers/");

        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mProfileSuccess.postValue(AppConstants.ERROR_LIST_USERS);
                mListUsers.postValue(null);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    mProfileSuccess.postValue(AppConstants.ERROR_LIST_USERS);
                    throw new IOException("Unexpected code " + response);
                }
                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if (success){
                    List<TUser> listaAux = mListUsers.getValue();
                    List<TUser> listFromResponse = getListFromResponse(res);
                    if(listFromResponse.isEmpty()){
                        return;
                    }
                    if (listaAux == null){
                        Log.d("UserListCallback", "La lista de usuarios estaba vacia inicialmente");
                        mListUsers.postValue(listFromResponse);
                    }
                    else{
                        listaAux.addAll(listFromResponse);
                        mListUsers.postValue(listaAux);
                    }
                    mProfileSuccess.postValue(AppConstants.LIST_USERS);//Importante que este despues del postValue de mUser
                }
                else{
                    mListUsers.postValue(null);
                    mProfileSuccess.postValue(AppConstants.ERROR_LIST_USERS);//Importante que este despues del postValue de mUser
                }

            }
        });

    }

    public void getCommentsAndHistoryCount(String nickname) {

        JSONObject jsonUser = new JSONObject();
        try {
            jsonUser.put("user", nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String postBodyString = jsonUser.toString();

        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(
                postBodyString,
                AppConstants.METHOD_POST, "/countfavorites&historyPlaces/"
        );

        Call call = simpleRequest.createCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mCountProfileCommentsAndHistory.postValue(null);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    mCountProfileCommentsAndHistory.postValue(null);
                    throw new IOException("Unexpected code " + response);
                }

                String res = response.body().string();
                if(!simpleRequest.isSuccessful(res)){
                    mCountProfileCommentsAndHistory.postValue(null);
                    throw new IOException("Unexpected code " + response);
                }
                else {
                    Pair<Integer, Integer> pair = UserRespositoryHelper.jsonPair(res);
                    mCountProfileCommentsAndHistory.postValue(pair);
                }
            }

        });
    }

    private List<TUser> getListFromResponse(String response){
        JSONObject jresponse = null;
        try {
            jresponse = new JSONObject(response);

            //dentro de get("users") contiene una lista de nicknames ["poti", "aaa", "pepe"]
            List<TUser> listUsers = new ArrayList<TUser>();
            JSONArray arrayUsers = jresponse.getJSONArray("users");
            for (int i = 0; i < arrayUsers.length(); i++) {
                TUser tUser = UserRespositoryHelper.jsonStringToUser(arrayUsers.getString(i));
                listUsers.add(tUser);
            }
            return listUsers;
        } catch (JSONException e) {
            e.printStackTrace();
            return  null;
        }
    }

    //Getter y Setters para LiveData

    public MutableLiveData<Boolean> getmSuccess() { return mSuccess; }

    public MutableLiveData<Integer> getProfileSuccess() { return mProfileSuccess; }

    public MutableLiveData<List<TUser>> getListUsers() { return mListUsers; }

    public void setmSuccess(MutableLiveData<Boolean> mSuccess) {
        this.mSuccess = mSuccess;
    }

    public MutableLiveData<TUser> getmUser() {
        return mUser;
    }


    public MutableLiveData<Pair<Integer, Integer>> getmCountProfileCommentsAndHistory() {
        return mCountProfileCommentsAndHistory;
    }

    public void clearListUsers() {
        mListUsers.setValue(new ArrayList<>());
    }
}