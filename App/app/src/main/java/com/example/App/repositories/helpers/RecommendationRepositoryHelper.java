package com.example.App.repositories.helpers;

import com.example.App.models.TPlace;
import com.example.App.models.TRecommendation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecommendationRepositoryHelper {

    public static List<TRecommendation> getListFromResponse(String res) {
        JSONObject jresponse = null;
        try {
            jresponse = new JSONObject(res);

            List<TRecommendation> recomendations = new ArrayList<>();
            JSONArray arrayPlaces = jresponse.getJSONArray("list");
            for (int i = 0; i < arrayPlaces.length(); i++) {
                TRecommendation tRecommendation = jsonStringToRecom(arrayPlaces.getString(i));
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

    public static TRecommendation jsonStringToRecom(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            TPlace place = PlaceRepositoryHelper.jsonStringToPlace(jsonObject.toString());
            return new TRecommendation(
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
