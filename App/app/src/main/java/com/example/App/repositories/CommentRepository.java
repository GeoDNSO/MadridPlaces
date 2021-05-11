    package com.example.App.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.App.networking.SimpleRequest;
import com.example.App.repositories.helpers.CommentRepositoryHelper;
import com.example.App.models.TComment;
import com.example.App.utilities.AppConstants;

import org.jetbrains.annotations.NotNull;
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

    public MutableLiveData<List<TComment>> getCommentsList() {
        return mCommentList;
    }

    public void listComments(String placeName, int page, int quant) {

        String postBodyString = CommentRepositoryHelper.dataToString(placeName, page, quant);
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
                    mCommentList.postValue(CommentRepositoryHelper.getListFromResponse(res));
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

        String postBodyString = CommentRepositoryHelper.dataToString(placeName, page, quant);
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
                        mCommentList.postValue(CommentRepositoryHelper.getListFromResponse(res));
                    }
                    else{
                        listaAux.addAll(CommentRepositoryHelper.getListFromResponse(res));
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

    //Para modificar el comentario, tambien se puede usar esta funcion
    public void newComment (String userName, String content, String placeName, float rate) { //Función que realiza la creación de un comentario con valoración
        String postBodyString = CommentRepositoryHelper.commentToString(userName, content, placeName, rate);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/newComment&Rate");
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
                TComment newComment = CommentRepositoryHelper.jsonStringToComment(res);
                if (success){
                    if (listaAux.isEmpty()){
                        List<TComment> nuevaLista = new ArrayList<>();
                        nuevaLista.add(newComment);
                        mCommentList.postValue(nuevaLista);
                    }
                    else{
                        CommentRepositoryHelper.searchAndDeleteList(listaAux, newComment);
                        listaAux.add(0,newComment);
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

    public void deleteComment(TComment comment, int position) {

        //TODO Únicamente creo la valoracion en la BD, no lo muestro en la APP, me da miedo crear un Tcomment con el comment vacío
        String postBodyString = deleteToString(comment.getId());
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_DELETE, "/location/deleteComment");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mSuccess.postValue(AppConstants.ERROR_DELETE_COMMENT);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (!response.isSuccessful()) {
                    mSuccess.postValue(AppConstants.ERROR_DELETE_COMMENT);
                    throw new IOException("Unexpected code " + response);
                }
                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if (success){
                    //TODO lo comentado estaba antes
                    List<TComment> listaAux = mCommentList.getValue();
                    //TComment newComment = jsonStringToComment(res);
                    listaAux.remove(position);
                    mCommentList.postValue(listaAux); //El problema es que carga la lista de cero y se empieza al inicio
                    //searchAndDeleteList(listaAux,newComment);

                    mSuccess.postValue(AppConstants.DELETE_COMMENT_OK);//Importante que este despues del postValue de mUser
                }
                else{
                    //mCommentList.postValue(null);
                    mSuccess.postValue(AppConstants.ERROR_DELETE_COMMENT);//Importante que este despues del postValue de mUser
                }
            }
        });
    }

    private String deleteToString(int id_comment) {
        JSONObject jsonName = new JSONObject();
        String infoString;
        try {
            jsonName.put("id_comment", id_comment);
        } catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = jsonName.toString();
        return infoString;
    }
}