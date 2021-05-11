package com.example.App.models.repositories.helpers;

import android.location.Location;
import android.util.Log;

import com.example.App.App;
import com.example.App.models.transfer.TPlace;
import com.example.App.services.LocationTrack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaceRepositoryHelper {

    //Utilidades JSON

    public static String placeNameJson(String placeName, String username) {
        JSONObject json = new JSONObject();
        String infoString = null;
        try {
            json.put("location", placeName);
            json.put("user", username);
        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = json.toString();

        return infoString;
    }

    public static String jsonToSendFrom(Double longitude, Double latitude, Double radius, Integer nPlaces, String nickname, String searchText, String category) {
        JSONObject json = new JSONObject();
        String infoString = null;
        try {
            json.put("user", nickname);
            json.put("latitude", latitude);
            json.put("longitude", longitude);
            json.put("radius", radius);
            json.put("nPlaces", nPlaces);
            json.put("search", searchText);
            json.put("category", category);
        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = json.toString();

        return infoString;
    }

    public static String jsonToSendFrom(Double longitude, Double latitude, Double radius, Integer nPlaces, String nickname, String search) {

        JSONObject json = new JSONObject();
        String infoString = null;
        try {
            json.put("user", nickname);
            json.put("latitude", latitude);
            json.put("longitude", longitude);
            json.put("radius", radius);
            json.put("nPlaces", nPlaces);
            json.put("search", search);
        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = json.toString();

        return infoString;
    }

    public static List<TPlace> getListFromResponse(String res) {
        JSONObject jresponse = null;
        try {
            jresponse = new JSONObject(res);

            List<TPlace> listPlaces = new ArrayList<>();
            JSONArray arrayPlaces = jresponse.getJSONArray("list");
            for (int i = 0; i < arrayPlaces.length(); i++) {
                TPlace tPlace = jsonStringToPlace(arrayPlaces.getString(i));
                listPlaces.add(tPlace);
            }
            return listPlaces;
        } catch (JSONException e) {
            e.printStackTrace();
            return  null;
        }
    }

    public static List<String> getCategoriesFromResponse(String res){
        JSONObject jresponse = null;
        try {
            jresponse = new JSONObject(res);

            List<String> listCategories = new ArrayList<>();
            JSONArray arrayCategories = jresponse.getJSONArray("list");
            for (int i = 0; i < arrayCategories.length(); i++) {
                listCategories.add(arrayCategories.getString(i));
            }
            return listCategories;
        } catch (JSONException e) {
            e.printStackTrace();
            return  null;
        }
    }

    public static TPlace jsonStringToPlace(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            String a = jsonObject.getString("road_number");

            List<String> jsonImagesList = jsonArrayImagesToStringList(jsonObject.getJSONArray("imageList"));
            Boolean placeIsFav = jsonObject.getString("favorite").equals("true");


            Double latitude = jsonObject.getDouble("coordinate_latitude");
            Double longitude = jsonObject.getDouble("coordinate_longitude");

            Location loc1 = new Location("");
            loc1.setLatitude(latitude);
            loc1.setLongitude(longitude);

            LocationTrack locationTrack = App.getInstance().getLocationTrack();

            Location loc2 = new Location("");
            loc2.setLatitude(locationTrack.getLatitude());
            loc2.setLongitude(locationTrack.getLongitude());

            double distanceToUser = ((float) loc1.distanceTo(loc2));
            Integer numberOfRatings = jsonObject.getInt("n_comments");
            String dateVisited = jsonObject.getString("visited");

            return new TPlace(
                    jsonObject.getString("name"),
                    jsonObject.getString("description"),
                    jsonObject.getDouble("coordinate_latitude"),
                    jsonObject.getDouble("coordinate_longitude"),
                    jsonImagesList,
                    jsonObject.getString("type_of_place"),
                    jsonObject.getString("city"),
                    jsonObject.getString("road_class"),
                    jsonObject.getString("road_name"),
                    jsonObject.getString("road_number"),
                    jsonObject.getString("zipcode"),
                    jsonObject.getString("affluence"),
                    jsonObject.getDouble("rate"),
                    placeIsFav,
                    distanceToUser,
                    numberOfRatings,
                    dateVisited);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> jsonArrayImagesToStringList(JSONArray jsonImageList) {
        ArrayList<String> lista = new ArrayList<>();


        for (int i=0;i<jsonImageList.length();i++){
            try {
                String imageURL = jsonImageList.getJSONObject(i).getString("image");
                String imageURLCorrected = jsonUrlCorrector(imageURL);
                lista.add(imageURLCorrected);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("ERROR", "jsonArrayImagesToStringList: Error al procesar array");
            }
        }
        return lista;
    }

    public static String jsonUrlCorrector(String json_data) {
        json_data = json_data.replace("\\", "");
        return json_data;
    }

    public static String pageAndQuantToSTring(int page, int quantity, String nickname, String search) {
        JSONObject jsonPageQuant = new JSONObject();
        String infoString;
        try {
            jsonPageQuant.put("page", page);
            jsonPageQuant.put("quant", quantity);
            jsonPageQuant.put("user", nickname);
            jsonPageQuant.put("search", search);
        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = jsonPageQuant.toString();

        return infoString;
    }

    public static String paramsToGetCategoriePlace(int page, int quantity, String nickname, String category, String search) {
        JSONObject json = new JSONObject();
        String infoString;
        try {
            json.put("page", page);
            json.put("quant", quantity);
            json.put("user", nickname);
            json.put("category", category);
            json.put("search", search);
        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = json.toString();

        return infoString;
    }

    public static String jsonInfoForFav(TPlace place, String username){

        JSONObject json = new JSONObject();
        String infoString;
        try {
            json.put("location", place.getName());
            json.put("user", username);
        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = json.toString();

        return infoString;
    }
}
