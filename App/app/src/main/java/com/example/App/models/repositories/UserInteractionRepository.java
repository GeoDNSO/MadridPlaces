package com.example.App.models.repositories;


import com.example.App.models.dao.SimpleRequest;
import com.example.App.utilities.AppConstants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class UserInteractionRepository extends Repository{

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

    private String jsonInfoForSendRecomendation(String userOrigin, String userDest, String place) {
        JSONObject json = new JSONObject();
        String infoString;
        try {
            json.put("userSrc", userOrigin);
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
