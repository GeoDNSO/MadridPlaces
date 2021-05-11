package com.example.App.repositories.helpers;

import android.util.Pair;

import com.example.App.models.TUser;

import org.json.JSONException;
import org.json.JSONObject;

public class UserRepositoryHelper {

    //Métodos Privados para JSONs

    //Crea un JSON con la información necesaria para el login: nickname y password
    public static String loginInfoToString(String nickname, String password){
        JSONObject jsonUser = new JSONObject();
        String infoString;
        try {
            jsonUser.put("nickname", nickname);
            jsonUser.put("password", password);
        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = jsonUser.toString();

        return infoString;
    }

    //Convierte String con formato json en usuario
    public static TUser jsonStringToUser(String jsonString){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            String image_profile = jsonObject.getString("profile_image");
            if(image_profile.equals("null")){
                image_profile = null;
            }
            return new TUser(jsonObject.getString("nickname"), jsonObject.getString("password")/*antes estaba con ""*/, jsonObject.getString("name"), jsonObject.getString("surname"), jsonObject.getString("email"), jsonObject.getString("gender"), jsonObject.getString("birth_date"), jsonObject.getString("city"), jsonObject.getString("rol"), image_profile);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String paramsToString(int page, int quantity, String searchText, int sortType) {
        JSONObject params = new JSONObject();
        String infoString;
        try {
            params.put("page", page);
            params.put("quant", quantity);
            params.put("search", searchText);
            params.put("filter_by", sortType);

        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = params.toString();

        return infoString;
    }

    public static Pair<Integer, Integer> jsonPair(String string) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(string);
            Integer countComments = jsonObject.getInt("nFavorites");
            Integer countHistoryPlaces = jsonObject.getInt("nVisited");

            return new Pair<>(countComments, countHistoryPlaces);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
