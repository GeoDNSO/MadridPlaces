package com.example.App.models.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.App.models.dao.SimpleRequest;
import com.example.App.models.transfer.TComment;
import com.example.App.models.transfer.TPlace;
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
import okhttp3.Request;
import okhttp3.Response;

public class CommentRepository extends Repository {

    private MutableLiveData<List<TComment>> mCommentList = new MutableLiveData<>();

    public LiveData<List<TComment>> getCommentsList() {
        return mCommentList;
    }

    public void listComments(String placeName, int page, int quant) {

        String postBodyString = dataToString(placeName, page, quant);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/showComments");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mSuccess.postValue(AppConstants.ERROR_LIST_COMMENTS);
                mCommentList.postValue(null);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (!response.isSuccessful()) {
                    mSuccess.postValue(AppConstants.ERROR_LIST_COMMENTS);
                    throw new IOException("Unexpected code " + response);
                }
                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if (success) {
                    mCommentList.postValue(getListFromResponse(res));
                    mSuccess.postValue(AppConstants.LIST_COMMENTS);//Importante que este despues del postValue de mUser
                } else {
                    Log.d("BBB", "not success");
                    mCommentList.postValue(null);
                    mSuccess.postValue(AppConstants.ERROR_LIST_COMMENTS);//Importante que este despues del postValue de mUser
                }
            }
        });
    }

    public void appendComments(String placeName, int page, int quant) {

        String postBodyString = dataToString(placeName, page, quant);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/showComments");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mSuccess.postValue(AppConstants.ERROR_LIST_COMMENTS);
                mCommentList.postValue(null);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (!response.isSuccessful()) {
                    mSuccess.postValue(AppConstants.ERROR_LIST_COMMENTS);
                    throw new IOException("Unexpected code " + response);
                }
                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                List<TComment> listaAux = mCommentList.getValue();
                if (success){
                    if (listaAux.isEmpty()){
                        Log.d("Info", "La lista esta vacia inicialmente");
                        mCommentList.postValue(getListFromResponse(res));
                    }
                    else{
                        listaAux.addAll(getListFromResponse(res));
                        mCommentList.postValue(listaAux);
                    }
                    mSuccess.postValue(AppConstants.LIST_COMMENTS);//Importante que este despues del postValue de mUser
                }
                else{
                    mCommentList.postValue(null);
                    mSuccess.postValue(AppConstants.ERROR_LIST_COMMENTS);//Importante que este despues del postValue de mUser
                }
            }
        });
    }

    public void newComment (String userName, String content, String placeName) { //Función que realiza la creación de un comentario sin valoración

        String postBodyString = commentToString(userName, content, placeName);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/newComment");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mSuccess.postValue(AppConstants.ERROR_LIST_COMMENTS);
                mCommentList.postValue(null);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (!response.isSuccessful()) {
                    mSuccess.postValue(AppConstants.ERROR_NEW_COMMENT);
                    throw new IOException("Unexpected code " + response);
                }
                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                List<TComment> listaAux = mCommentList.getValue();
                if (success){
                    if (listaAux.isEmpty()){
                        mCommentList.postValue(getListFromResponse(res));
                    }
                    else{
                        listaAux.add(jsonStringToCommentNoRate(res));
                        mCommentList.postValue(listaAux);
                    }
                    mSuccess.postValue(AppConstants.NEW_COMMENT);//Importante que este despues del postValue de mUser
                }
                else{
                    mCommentList.postValue(null);
                    mSuccess.postValue(AppConstants.ERROR_NEW_COMMENT);//Importante que este despues del postValue de mUser
                }
            }
        });
    }

    private String commentToString(String userName, String content, String placeName){
        JSONObject jsonName = new JSONObject();
        String infoString;
        try {
            jsonName.put("location", placeName);
            jsonName.put("user", userName);
            jsonName.put("comment", content);
        } catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = jsonName.toString();
        return infoString;
    }
    private String dataToString(String placeName, int page, int quant) {
        JSONObject jsonName = new JSONObject();
        String infoString;
        try {
            jsonName.put("location", placeName);
            jsonName.put("page", page);
            jsonName.put("quant", quant);
        } catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = jsonName.toString();
        return infoString;
    }

    private List<TComment> getListFromResponse(String res) {
        JSONObject jresponse = null;
        try {
            jresponse = new JSONObject(res);

            List<TComment> listOfComments = new ArrayList<>();
            JSONArray arrayComments = jresponse.getJSONArray("listComments");
            for (int i = 0; i < arrayComments.length(); i++) {
                TComment tComment = jsonStringToComment(arrayComments.getString(i));
                listOfComments.add(tComment);
            }
            return listOfComments;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    private TComment jsonStringToCommentNoRate(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            return new TComment(
                    "Imagen Perfil",
                    jsonObject.getString("user"),
                    jsonObject.getString("comment"),
                    jsonObject.getString("created"),
                    0.0);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private TComment jsonStringToComment(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            return new TComment(
                    "Imagen Perfil",
                    jsonObject.getString("user"),
                    jsonObject.getString("comment"),
                    jsonObject.getString("created"),
                    jsonObject.getDouble("rate"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}