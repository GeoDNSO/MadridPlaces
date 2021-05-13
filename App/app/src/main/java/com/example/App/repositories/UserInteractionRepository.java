package com.example.App.repositories;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.App.networking.SimpleRequest;
import com.example.App.repositories.helpers.UserInteractionRepositoryHelper;
import com.example.App.models.TRecommendation;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.ControlValues;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class UserInteractionRepository extends Repository{

    private MutableLiveData<List<TRecommendation>> mRecommendationsList = new MutableLiveData<>();
    private MutableLiveData<List<TRecommendation>> mPendingRecommendationsList = new MutableLiveData<>();



    public MutableLiveData<List<TRecommendation>> getmRecommendationsList() {
        return mRecommendationsList;
    }

    public MutableLiveData<List<TRecommendation>> getmPendingRecommendationsList() {
        return mPendingRecommendationsList;
    }


    class RecommendationsListCallBack implements Callback {

        private SimpleRequest simpleRequest;
        private MutableLiveData<List<TRecommendation>> recomList;


        public RecommendationsListCallBack(SimpleRequest simpleRequest, MutableLiveData<List<TRecommendation>> recomList){
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
            mSuccess.postValue(ControlValues.LIST_REC_FAIL);
            recomList.postValue(null);
            call.cancel();
        }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            this.sleep(500);//Para simular la carga...

            if (!response.isSuccessful()) {
                mSuccess.postValue(ControlValues.LIST_REC_FAIL);
                throw new IOException("Unexpected code " + response);
            }
            String res = response.body().string();
            boolean success = simpleRequest.isSuccessful(res);

            if(!success){
                Log.d("RecomListCallback", "Not success");
                recomList.postValue(null);
                mSuccess.postValue(ControlValues.LIST_REC_FAIL);//Importante que este despues del postValue de mUser

                return;
            }
            //si no hubo problemas...
            List<TRecommendation> listaAux = recomList.getValue();
            List<TRecommendation> listaFromResponse = UserInteractionRepositoryHelper.getListFromResponse(res);
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
            mSuccess.postValue(ControlValues.LIST_REC_OK);//Importante que este despues del postValue de recomList
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
                mSuccess.postValue(ControlValues.PENDING_REC_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(!response.isSuccessful()) {
                    mSuccess.postValue(ControlValues.PENDING_REC_FAIL);
                    throw new IOException("Unexpected code " + response);
                }

                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if(success) {
                    mSuccess.postValue(ControlValues.ACCEPT_REC_OK);
                }
                else {
                    mSuccess.postValue(ControlValues.PENDING_REC_FAIL);
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
                mSuccess.postValue(ControlValues.PENDING_REC_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(!response.isSuccessful()) {
                    mSuccess.postValue(ControlValues.PENDING_REC_FAIL);
                    throw new IOException("Unexpected code " + response);
                }

                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if(success) {
                    mSuccess.postValue(ControlValues.DENY_REC_OK);
                }
                else {
                    mSuccess.postValue(ControlValues.PENDING_REC_FAIL);
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
                mSuccess.postValue(ControlValues.SEND_REC_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(!response.isSuccessful()) {
                    mSuccess.postValue(ControlValues.SEND_REC_FAIL);
                    throw new IOException("Unexpected code " + response);
                }

                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if(success) {
                    mSuccess.postValue(ControlValues.SEND_REC_OK);
                }
                else {
                    mSuccess.postValue(ControlValues.SEND_REC_FAIL);
                }
            }
        });
    }

}
