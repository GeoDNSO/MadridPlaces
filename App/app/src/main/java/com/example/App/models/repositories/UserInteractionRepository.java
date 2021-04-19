package com.example.App.models.repositories;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.App.models.dao.SimpleRequest;
import com.example.App.models.transfer.TRecomendation;
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

    public MutableLiveData<List<TRecomendation>> getmRecommendationsList() {
        return mRecommendationsList;
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
            List<TRecomendation> listaFromResponse = getListFromResponse(res);
            if(listaFromResponse == null){
                Log.d("PLACE_REPO", "La lista JSON convertida es NULO, MIRAR...");
                return;
            }
            if(listaFromResponse.isEmpty()){
                return;
            }
            if (listaAux == null){
                recomList.postValue(getListFromResponse(res));
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

        String postBodyString = pageAndQuantToSTring(page, quantity, nickname);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/recommendations/listRecommendations");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new UserInteractionRepository.RecommendationsListCallBack(simpleRequest, mRecommendationsList));
    }

    private String pageAndQuantToSTring(int page, int quantity, String nickname) {
        JSONObject jsonPageQuant = new JSONObject();
        String infoString;
        try {
            jsonPageQuant.put("page", page);
            jsonPageQuant.put("quant", quantity);
            jsonPageQuant.put("user", nickname);
        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = jsonPageQuant.toString();

        return infoString;
    }

    public void sendRecomendation(String userOrigin, String userDest, String place) {
        String postBodyString = jsonInfoForSendRecomendation(userOrigin, userDest, place);

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

    private List<TRecomendation> getListFromResponse(String res) {
        JSONObject jresponse = null;
        try {
            jresponse = new JSONObject(res);

            List<TRecomendation> recomendations = new ArrayList<>();
            JSONArray arrayPlaces = jresponse.getJSONArray("list");
            for (int i = 0; i < arrayPlaces.length(); i++) {
                TRecomendation tRecommendation = jsonStringToRecom(arrayPlaces.getString(i));
                recomendations.add(tRecommendation);
            }
            return recomendations;
        } catch (JSONException e) {
            e.printStackTrace();
            return  null;
        }
    }

    private TRecomendation jsonStringToRecom(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);

            return new TRecomendation(
                    jsonObject.getString("userSrc"),
                    jsonObject.getString("userDest"),
                    jsonObject.getString("location"),
                    jsonObject.getString("state"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String jsonInfoForSendRecomendation(String userOrigin, String userDest, String place) {
        JSONObject json = new JSONObject();
        String infoString;
        try {
            json.put("userSrc", "Jin"); //TODO Está a pelo
            json.put("userDst", userDest);
            json.put("location", place);
        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = json.toString();

        return infoString;
    }


}
