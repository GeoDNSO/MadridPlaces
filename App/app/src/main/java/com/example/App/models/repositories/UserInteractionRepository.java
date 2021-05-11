package com.example.App.models.repositories;


import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.App.App;
import com.example.App.models.dao.SimpleRequest;
import com.example.App.models.repositories.helpers.PlaceRepositoryHelper;
import com.example.App.models.repositories.helpers.UserInteractionRepositoryHelper;
import com.example.App.models.transfer.TPlace;
import com.example.App.models.transfer.TRecomendation;
import com.example.App.services.LocationTrack;
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

public class UserInteractionRepository extends Repository{

    private MutableLiveData<List<TRecomendation>> mRecommendationsList = new MutableLiveData<>();
    private MutableLiveData<List<TRecomendation>> mPendingRecommendationsList = new MutableLiveData<>();
    private MutableLiveData<Integer> mAcceptRecommendation = new MutableLiveData<Integer>();
    private MutableLiveData<Integer> mDenyRecommendation = new MutableLiveData<Integer>();


    public MutableLiveData<List<TRecomendation>> getmRecommendationsList() {
        return mRecommendationsList;
    }

    public MutableLiveData<List<TRecomendation>> getmPendingRecommendationsList() {
        return mPendingRecommendationsList;
    }

    public MutableLiveData<Integer> getmAcceptRecommendation(){
        return mAcceptRecommendation;
    }

    public MutableLiveData<Integer> getmDenyRecommendation(){
        return mDenyRecommendation;
    }

    class RecommendationsListCallBack implements Callback {

        private SimpleRequest simpleRequest;
        private MutableLiveData<List<TRecomendation>> recomList;


        public RecommendationsListCallBack(SimpleRequest simpleRequest, MutableLiveData<List<TRecomendation>> recomList){
            this.simpleRequest = simpleRequest;
            this.recomList = recomList;
        }

            private void sleep(long milis){
            try {
                Thread.sleep(milis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            e.printStackTrace();
            Log.d("REC_REPOSITORY", "FAILURE, error above");
            mSuccess.postValue(AppConstants.LIST_REC_FAIL);
            recomList.postValue(null);
            call.cancel();
        }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            this.sleep(500);//Para simular la carga...

            if (!response.isSuccessful()) {
                mSuccess.postValue(AppConstants.LIST_REC_FAIL);
                throw new IOException("Unexpected code " + response);
            }
            String res = response.body().string();
            boolean success = simpleRequest.isSuccessful(res);

            if(!success){
                Log.d("RecomListCallback", "Not success");
                recomList.postValue(null);
                mSuccess.postValue(AppConstants.LIST_REC_FAIL);//Importante que este despues del postValue de mUser

                return;
            }
            //si no hubo problemas...
            List<TRecomendation> listaAux = recomList.getValue();
            List<TRecomendation> listaFromResponse = UserInteractionRepositoryHelper.getListFromResponse(res);
            if(listaFromResponse == null){
                Log.d("PLACE_REPO", "La lista JSON convertida es NULO, MIRAR...");
                return;
            }
            if(listaFromResponse.isEmpty()){
                return;
            }
            if (listaAux == null){
                recomList.postValue(UserInteractionRepositoryHelper.getListFromResponse(res));
            }
            else{
                listaAux.addAll(listaFromResponse);
                recomList.postValue(listaAux);
            }
            mSuccess.postValue(AppConstants.LIST_REC_OK);//Importante que este despues del postValue de recomList
        }
    }

    //lista lugares de quantity en quantity en función de page alfabeticamente
    // Ej: quantity = 100 -> (page:0 = 1-100, page:1 = 101-200...)
    public void listRecom(int page, int quantity, String nickname) {

        String postBodyString = UserInteractionRepositoryHelper.pageAndQuantToSTring(page, quantity, nickname);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/recommendations/listRecommendationsSent");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new UserInteractionRepository.RecommendationsListCallBack(simpleRequest, mRecommendationsList));
    }

    public void acceptPendingRecom(String placeName, String userOrigin, String userDest){
        String postBodyString = UserInteractionRepositoryHelper.jsonInfoForSendRecomendation(userOrigin, userDest, placeName);

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(
                postBodyString,
                AppConstants.METHOD_POST, "/recommendations/acceptRecommendation"
        );

        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mAcceptRecommendation.postValue(AppConstants.PENDING_REC_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(!response.isSuccessful()) {
                    mAcceptRecommendation.postValue(AppConstants.PENDING_REC_FAIL);
                    throw new IOException("Unexpected code " + response);
                }

                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if(success) {
                    mAcceptRecommendation.postValue(AppConstants.ACCEPT_REC_OK);
                }
                else {
                    mAcceptRecommendation.postValue(AppConstants.PENDING_REC_FAIL);
                }
            }
        });
    }

    public void denyPendingRecom(String placeName, String userOrigin, String userDest){
        String postBodyString = UserInteractionRepositoryHelper.jsonInfoForSendRecomendation(userOrigin, userDest, placeName);

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(
                postBodyString,
                AppConstants.METHOD_DELETE, "/recommendations/deleteRecommendation"
        );

        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mDenyRecommendation.postValue(AppConstants.PENDING_REC_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(!response.isSuccessful()) {
                    mDenyRecommendation.postValue(AppConstants.PENDING_REC_FAIL);
                    throw new IOException("Unexpected code " + response);
                }

                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if(success) {
                    mDenyRecommendation.postValue(AppConstants.DENY_REC_OK);
                }
                else {
                    mDenyRecommendation.postValue(AppConstants.PENDING_REC_FAIL);
                }
            }
        });
    }

    public void listPendingRecom(int page, int quantity, String nickname) {

        String postBodyString = UserInteractionRepositoryHelper.pageAndQuantToSTring(page, quantity, nickname);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/recommendations/listPendingRecommendations");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new UserInteractionRepository.RecommendationsListCallBack(simpleRequest, mPendingRecommendationsList));
    }

    public void sendRecomendation(String userOrigin, String userDest, String place) {
        String postBodyString = UserInteractionRepositoryHelper.jsonInfoForSendRecomendation(userOrigin, userDest, place);

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(
                postBodyString,
                AppConstants.METHOD_POST, "/recommendations/newRecommentation"
        );

        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mSuccess.postValue(AppConstants.SEND_REC_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(!response.isSuccessful()) {
                    mSuccess.postValue(AppConstants.SEND_REC_FAIL);
                    throw new IOException("Unexpected code " + response);
                }

                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if(success) {
                    mSuccess.postValue(AppConstants.SEND_REC_OK);
                }
                else {
                    mSuccess.postValue(AppConstants.SEND_REC_FAIL);
                }
            }
        });
    }

}
