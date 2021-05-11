package com.example.App.models.repositories.helpers;

import com.example.App.models.transfer.TPlace;
import com.example.App.models.transfer.TRecomendation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserInteractionRepositoryHelper {

    public static List<TRecomendation> getListFromResponse(String res) {
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

    public static String jsonInfoForSendRecomendation(String userOrigin, String userDest, String place) {
        JSONObject json = new JSONObject();
        String infoString;
        try {
            json.put("userSrc", userOrigin); //TODO Está a pelo
            json.put("userDst", userDest);
            json.put("location", place);
        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = json.toString();

        return infoString;
    }

    public static TRecomendation jsonStringToRecom(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            TPlace place = PlaceRepositoryHelper.jsonStringToPlace(jsonObject.toString());
            return new TRecomendation(
                    jsonObject.getString("userSrc"),
                    jsonObject.getString("userDst"),
                    place,
                    jsonObject.getString("state"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String pageAndQuantToSTring(int page, int quantity, String nickname) {
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

   
}
