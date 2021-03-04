package com.example.App.models.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.App.models.dao.SimpleRequest;
import com.example.App.models.transfer.TPlace;
import com.example.App.models.transfer.TUser;
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

public class PlaceRepository extends Repository{

    private MutableLiveData<List<TPlace>> mPlacesList = new MutableLiveData<>();
    private MutableLiveData<TPlace> mPlace = new MutableLiveData<>();

    public void getPlace() {

    }

    public LiveData<List<TPlace>> getPlacesList(){ return mPlacesList; }

    //lista lugares de quantity en quantity en función de page alfabeticamente
    // Ej: quantity = 100 -> (page:0 = 1-100, page:1 = 101-200...)
    public void listPlaces(int page, int quantity) {

        String postBodyString = pageAndQuantToSTring(page, quantity);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/listLocations");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mSuccess.postValue(AppConstants.ERROR_LIST_PLACES);
                Log.d("CCC", "FAILURE GORDO");
                mPlacesList.postValue(null);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!response.isSuccessful()) {
                    mSuccess.postValue(AppConstants.ERROR_LIST_PLACES);
                    throw new IOException("Unexpected code " + response);
                }
                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if (success){
                    mPlacesList.postValue(getListFromResponse(res));
                    mSuccess.postValue(AppConstants.LIST_PLACES);//Importante que este despues del postValue de mUser
                }
                else{
                    Log.d("BBB", "not success");
                    mPlacesList.postValue(null);
                    mSuccess.postValue(AppConstants.ERROR_LIST_PLACES);//Importante que este despues del postValue de mUser
                }
            }
        });
    }

    //lista lugares de quantity en quantity en función de page alfabeticamente añadiendo anteriores
    // Ej: quantity = 100 -> (page:0 = 1-100, page:1 = 101-200...)
    public void appendPlaces(int page, int quantity) {

        String postBodyString = pageAndQuantToSTring(page, quantity);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/listLocations");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mSuccess.postValue(AppConstants.ERROR_LIST_PLACES);
                mPlacesList.postValue(null);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!response.isSuccessful()) {
                    mSuccess.postValue(AppConstants.ERROR_LIST_PLACES);
                    throw new IOException("Unexpected code " + response);
                }
                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                List<TPlace> listaAux = mPlacesList.getValue();
                if (success){
                    if (listaAux.isEmpty()){
                        Log.d("Info", "La lista esta vacia inicialmente");
                        mPlacesList.postValue(getListFromResponse(res));
                    }
                    else{
                        listaAux.addAll(getListFromResponse(res));
                        mPlacesList.postValue(listaAux);
                    }
                    mSuccess.postValue(AppConstants.LIST_PLACES);//Importante que este despues del postValue de mUser
                }
                else{
                    mPlacesList.postValue(null);
                    mSuccess.postValue(AppConstants.ERROR_LIST_PLACES);//Importante que este despues del postValue de mUser
                }
            }
        });
    }

    private String pageAndQuantToSTring(int page, int quantity) {
        JSONObject jsonPageQuant = new JSONObject();
        String infoString;
        try {
            jsonPageQuant.put("page", page);
            jsonPageQuant.put("quant", quantity);
        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = jsonPageQuant.toString();

        return infoString;
    }

    public void closePlaces() {

    }

    public void valoratedPlaces() {

    }

    public void twitterPlaces() {

    }

    public void valoratePlace() {

    }

    public void commentPlace() {

    }

    public void historyOfPlaces() {

    }

    private List<TPlace> getListFromResponse(String res) {
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

    private TPlace jsonStringToPlace(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            List<String> jsonImagesList = jsonArrayImagesToStringList(jsonObject.getJSONArray("imageList"));
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
                    false);
            //return new TPlace(jsonObject.getString("nickname"), jsonObject.getString("password")/*antes estaba con ""*/, jsonObject.getString("name"), jsonObject.getString("surname"), jsonObject.getString("email"), jsonObject.getString("gender"), jsonObject.getString("birth_date"), jsonObject.getString("city"), jsonObject.getString("rol"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<String> jsonArrayImagesToStringList(JSONArray jsonImageList) {
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

    private String jsonUrlCorrector(String json_data) {
        json_data = json_data.replace("\\", "");
        return json_data;
    }
}
