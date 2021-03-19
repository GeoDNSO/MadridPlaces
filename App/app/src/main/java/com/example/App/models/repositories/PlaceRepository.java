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
    private MutableLiveData<Boolean> mBooleanPlace = new MutableLiveData<>();
    private MutableLiveData<List<TPlace>> mPlacesList = new MutableLiveData<>();
    private MutableLiveData<List<TPlace>> mHistoryPlacesList = new MutableLiveData<>();
    private MutableLiveData<List<TPlace>> mCategoriesPlacesList = new MutableLiveData<>();
    private MutableLiveData<List<String>> mCategoriesList = new MutableLiveData<>();
    private MutableLiveData<TPlace> mPlace = new MutableLiveData<>();

    public void getPlace() {

    }

    public LiveData<Boolean> getBooleanPlace(){ return mBooleanPlace; }
    public LiveData<List<TPlace>> getPlacesList(){ return mPlacesList; }
    public LiveData<List<TPlace>> getHistoryPlacesList(){ return mHistoryPlacesList; }
    public LiveData<List<String>> getCategoriesList(){ return mCategoriesList; }
    public MutableLiveData<List<TPlace>> getCategoriesPlacesList() { return mCategoriesPlacesList; }


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

    public void addPlace(TPlace place){
        String postBodyString = place.jsonToString();
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(
                postBodyString,
                AppConstants.METHOD_POST, "/location/newLocation"
        );
        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mSuccess.postValue(AppConstants.ERROR_ADD_PLACE);
                mBooleanPlace.postValue(false);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                boolean sucess = simpleRequest.isSuccessful(response);
                if(sucess){
                    mSuccess.postValue(AppConstants.ADD_PLACE);
                    mBooleanPlace.postValue(true);
                }
                else{
                    mSuccess.postValue(AppConstants.ERROR_ADD_PLACE);
                    mBooleanPlace.postValue(false);
                }
            }
        });
    }

    public void modifyPlace(TPlace place){
        String postBodyString = place.jsonToString();
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(
                postBodyString,
                AppConstants.METHOD_PUT, "/location/modifyLocation"
        );
        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mSuccess.postValue(AppConstants.ERROR_MODIFY_PLACE);
                mBooleanPlace.postValue(false);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                boolean sucess = simpleRequest.isSuccessful(response);
                if(sucess){
                    mSuccess.postValue(AppConstants.MODIFY_PLACE);
                    mBooleanPlace.postValue(true);
                }
                else{
                    mSuccess.postValue(AppConstants.ERROR_MODIFY_PLACE);
                    mBooleanPlace.postValue(false);
                }
            }
        });
    }

    public void deletePlace(String placeName){
        JSONObject jsonPlace = new JSONObject();

        try {
            jsonPlace.put("name", placeName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String postBodyString = jsonPlace.toString();

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_DELETE, "/location/deleteLocation");

        Call call = simpleRequest.createCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mSuccess.postValue(AppConstants.ERROR_DETAIL_PLACE);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mSuccess.postValue(AppConstants.ERROR_DETAIL_PLACE);
                    throw new IOException("Unexpected code " + response);
                }
                Boolean success = simpleRequest.isSuccessful(response);
                if(success){
                    mSuccess.postValue(AppConstants.DELETE_PLACE);
                }
                else{
                    mSuccess.postValue(AppConstants.ERROR_DETAIL_PLACE);
                }

            }
        });
    }

    public void getCategories() {
        String postBodyString = "";

        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/categories");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mSuccess.postValue(AppConstants.ERROR_GET_CATEGORIES);
                mCategoriesList.postValue(null);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mSuccess.postValue(AppConstants.ERROR_GET_CATEGORIES);
                    mCategoriesList.postValue(null);
                    throw new IOException("Unexpected code " + response);
                }
                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if (success){
                    mSuccess.postValue(AppConstants.GET_CATEGORIES);//Importante que este despues del postValue de mUser
                    mCategoriesList.postValue(getCategoriesFromResponse(res));
                }
                else{
                    mSuccess.postValue(AppConstants.ERROR_GET_CATEGORIES);//Importante que este despues del postValue de mUser
                    mCategoriesList.postValue(null);
                }
            }
        });
    }

    public void addPlaceImages(String placeName, List<String> listImages){

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

    private String paramsToGetCategoriePlace(int page, int quantity, String category) {
        JSONObject json = new JSONObject();
        String infoString;
        try {
            json.put("page", page);
            json.put("quant", quantity);
            json.put("category", category);
        }catch (JSONException e) {
            e.printStackTrace();
            infoString = "error";
        }
        infoString = json.toString();

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

    public void historyListPlaces(int page, int quantity) {
        //TODO devuelve la lista de lugares. Solo es necesario la lista visitada.
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
                mHistoryPlacesList.postValue(null);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    Thread.sleep(5000);
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
                    mHistoryPlacesList.postValue(getListFromResponse(res));
                    mSuccess.postValue(AppConstants.LIST_PLACES);//Importante que este despues del postValue de mUser
                }
                else{
                    Log.d("BBB", "not success");
                    mHistoryPlacesList.postValue(null);
                    mSuccess.postValue(AppConstants.ERROR_LIST_PLACES);//Importante que este despues del postValue de mUser
                }
            }
        });
    }

    public void appendHistoryPlaces(int page, int quantity) {

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
                mHistoryPlacesList.postValue(null);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!response.isSuccessful()) {
                    mSuccess.postValue(AppConstants.ERROR_LIST_PLACES);
                    throw new IOException("Unexpected code " + response);
                }
                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                List<TPlace> listaAux = mHistoryPlacesList.getValue();
                if (success){
                    if (listaAux.isEmpty()){
                        Log.d("Info", "La lista esta vacia inicialmente");
                        mHistoryPlacesList.postValue(getListFromResponse(res));
                    }
                    else{
                        listaAux.addAll(getListFromResponse(res));
                        mHistoryPlacesList.postValue(listaAux);
                    }
                    mSuccess.postValue(AppConstants.LIST_PLACES);//Importante que este despues del postValue de mUser
                }
                else{
                    mHistoryPlacesList.postValue(null);
                    mSuccess.postValue(AppConstants.ERROR_LIST_PLACES);//Importante que este despues del postValue de mUser
                }
            }
        });
    }

    public void listPlacesCategories(int page, int quantity, String category) {

        String postBodyString = paramsToGetCategoriePlace(page, quantity, category);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/listByCategory");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mSuccess.postValue(AppConstants.ERROR_LIST_PLACES);
                Log.d("CCC", "FAILURE GORDO");
                mCategoriesPlacesList.postValue(null);
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
                    mCategoriesPlacesList.postValue(getListFromResponse(res));
                    Log.i("XX", "Categorias tiene valor");
                    mSuccess.postValue(AppConstants.LIST_PLACES);//Importante que este despues del postValue de mUser
                }
                else{
                    mCategoriesPlacesList.postValue(null);
                    mSuccess.postValue(AppConstants.ERROR_LIST_PLACES);//Importante que este despues del postValue de mUser
                }
            }
        });
    }

    //lista lugares de quantity en quantity en función de page alfabeticamente añadiendo anteriores
    // Ej: quantity = 100 -> (page:0 = 1-100, page:1 = 101-200...)
    public void appendPlacesCategories(int page, int quantity, String category) {

        String postBodyString = paramsToGetCategoriePlace(page, quantity, category);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/listByCategory");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mSuccess.postValue(AppConstants.ERROR_LIST_PLACES);
                mCategoriesPlacesList.postValue(null);
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
                        mCategoriesPlacesList.postValue(getListFromResponse(res));
                    }
                    else{
                        listaAux.addAll(getListFromResponse(res));
                        mCategoriesPlacesList.postValue(listaAux);
                    }
                    mSuccess.postValue(AppConstants.LIST_PLACES);//Importante que este despues del postValue de mUser
                }
                else{
                    mCategoriesPlacesList.postValue(null);
                    mSuccess.postValue(AppConstants.ERROR_LIST_PLACES);//Importante que este despues del postValue de mUser
                }
            }
        });
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

    public List<String> getCategoriesFromResponse(String res){
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

    private TPlace jsonStringToPlace(String jsonString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            String a = jsonObject.getString("road_number");

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
