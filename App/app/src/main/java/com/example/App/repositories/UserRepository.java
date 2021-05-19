package com.example.App.repositories;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MutableLiveData;

import com.example.App.networking.SimpleRequest;
import com.example.App.repositories.helpers.UserRepositoryHelper;
import com.example.App.models.TUser;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.ControlValues;

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

public class UserRepository extends Repository{

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
                mSuccess.postValue(ControlValues.REGISTER_USER_FAIL);
                call.cancel();
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                mSuccess.postValue(ControlValues.REGISTER_USER_OK);
            }
        });
    }

    public void loginUser(String nickname, String password) {

        String postBodyString = UserRepositoryHelper.loginInfoToString(nickname, password);

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/login/");

        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mSuccess.postValue(ControlValues.LOGIN_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    mSuccess.postValue(ControlValues.LOGIN_FAIL);
                    throw new IOException("Unexpected code " + response);
                }
                String res = response.body().string();

                boolean success = simpleRequest.isSuccessful(res);

                if (success){
                    mUser.postValue(UserRepositoryHelper.jsonStringToUser(res));
                    mSuccess.postValue(ControlValues.LOGIN_OK);//Importante que este despues del postValue de mUser
                    return;
                }
                mUser.postValue(null);
                mSuccess.postValue(ControlValues.LOGIN_WRONG_CREDENTIALS);//Importante que este despues del postValue de mUser

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
                mSuccess.postValue(ControlValues.DELETE_PROFILE_FAILED);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mSuccess.postValue(ControlValues.DELETE_PROFILE_FAILED);
                    throw new IOException("Unexpected code " + response);
                }
                Boolean success = simpleRequest.isSuccessful(response);
                if(success){
                    mSuccess.postValue(ControlValues.DELETE_PROFILE_OK);
                }
                else{
                    mSuccess.postValue(ControlValues.PROFILE_FAILED);
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
                mSuccess.postValue(ControlValues.MODIFY_USER_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mSuccess.postValue(ControlValues.MODIFY_USER_FAIL);
                    throw new IOException("Unexpected code " + response);
                }
                String res = response.body().string();

                mUser.postValue(UserRepositoryHelper.jsonStringToUser(res));
                mSuccess.postValue(ControlValues.MODIFY_USER_OK);
            }
        });

    }

    public void listUsers(int page, int quantum, String searchText, int sortType) {
        String postBodyString = UserRepositoryHelper.paramsToString(page, quantum, searchText, sortType);

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/listUsers/");

        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mSuccess.postValue(ControlValues.LIST_USERS_FAILED);
                mListUsers.postValue(null);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    mSuccess.postValue(ControlValues.LIST_USERS_FAILED);
                    throw new IOException("Unexpected code " + response);
                }
                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if (success){
                    List<TUser> listaAux = mListUsers.getValue();
                    List<TUser> listFromResponse = UserRepositoryHelper.getListFromResponse(res);
                    if(listFromResponse.isEmpty()){
                        mSuccess.postValue(ControlValues.NO_MORE_USERS_TO_LIST);
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
                    mSuccess.postValue(ControlValues.LIST_USERS_OK);
                }
                else{
                    mListUsers.postValue(null);
                    mSuccess.postValue(ControlValues.LIST_USERS_FAILED);
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
                mSuccess.postValue(ControlValues.GET_COMMENT_AND_HISTORY_FAIL);
                mCountProfileCommentsAndHistory.postValue(null);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    mCountProfileCommentsAndHistory.postValue(null);
                    mSuccess.postValue(ControlValues.GET_COMMENT_AND_HISTORY_FAIL);
                    throw new IOException("Unexpected code " + response);
                }

                String res = response.body().string();
                if(!simpleRequest.isSuccessful(res)){
                    mCountProfileCommentsAndHistory.postValue(null);
                    mSuccess.postValue(ControlValues.GET_COMMENT_AND_HISTORY_FAIL);
                    throw new IOException("Unexpected code " + response);
                }
                else {
                    Pair<Integer, Integer> pair = UserRepositoryHelper.jsonPair(res);
                    mCountProfileCommentsAndHistory.postValue(pair);
                    mSuccess.postValue(ControlValues.GET_COMMENT_AND_HISTORY_OK);
                }
            }

        });
    }

    //Getter y Setters para LiveData

    public MutableLiveData<Integer> getmSuccess() { return mSuccess; }

    public MutableLiveData<List<TUser>> getListUsers() { return mListUsers; }

    public MutableLiveData<TUser> getmUser() {
        return mUser;
    }

    public MutableLiveData<Pair<Integer, Integer>> getmCountProfileCommentsAndHistory() {
        return mCountProfileCommentsAndHistory;
    }

    public void clearListUsers() {
        mListUsers.setValue(new ArrayList<>());
    }

    public void setmSuccess(MutableLiveData<Integer> mSuccess) {
        this.mSuccess = mSuccess;
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
                mSuccess.postValue(ControlValues.GET_USER_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    mSuccess.postValue(ControlValues.GET_USER_FAIL);
                    throw new IOException("Unexpected code " + response);
                }

                TUser user = UserRepositoryHelper.jsonStringToUser(response.body().string());
                mUser.postValue(user);
                mSuccess.postValue(ControlValues.GET_USER_OK);
            }
        });
    }
}