package com.example.App.repositories;

import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.App.networking.SimpleRequest;
import com.example.App.repositories.helpers.PlaceRepositoryHelper;
import com.example.App.models.TPlace;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.ControlValues;

import org.jetbrains.annotations.NotNull;
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
    private MutableLiveData<List<TPlace>> mTwitterPlacesList = new MutableLiveData<>();
    private MutableLiveData<List<TPlace>> mNearestPlacesList = new MutableLiveData<>();
    private MutableLiveData<List<TPlace>> mFavouritesPlacesList = new MutableLiveData<>();
    private MutableLiveData<List<TPlace>> mCategoriesPlacesList = new MutableLiveData<>();
    private MutableLiveData<List<String>> mCategoriesList = new MutableLiveData<>();
    private MutableLiveData<TPlace> mPlace = new MutableLiveData<>();
    private MutableLiveData<List<TPlace>> mPlaceVisited = new MutableLiveData<>();
    private MutableLiveData<List<TPlace>> mPlacesPendingToVisit = new MutableLiveData<>();

    //Callback personalizado tanto para las listas de lugares
    class PlaceListCallBack implements Callback{

        private SimpleRequest simpleRequest;
        private MutableLiveData<List<TPlace>> placeList;

        public PlaceListCallBack(SimpleRequest simpleRequest, MutableLiveData<List<TPlace>> placeList){
            this.simpleRequest = simpleRequest;
            this.placeList = placeList;
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            e.printStackTrace();
            Log.d("PLACE_REPOSITORY", "FAILURE, error above");
            mSuccess.postValue(ControlValues.LIST_PLACES_FAIL);
            placeList.postValue(null);
            call.cancel();
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            //sleep(500);//Para simular la carga...
            if (!response.isSuccessful()) {
                mSuccess.postValue(ControlValues.LIST_PLACES_FAIL);
                throw new IOException("Unexpected code " + response);
            }
            String res = response.body().string();
            boolean success = simpleRequest.isSuccessful(res);

            if(!success){
                Log.d("PlaceListCallback", "Result got: Not Success");
                placeList.postValue(null);
                mSuccess.postValue(ControlValues.LIST_PLACES_FAIL);
                return;
            }
            //si no hubo problemas...
            List<TPlace> auxList = placeList.getValue();
            List<TPlace> listFromResponse = PlaceRepositoryHelper.getListFromResponse(res);
            if(listFromResponse == null){
                Log.d("PlaceListCallback", "Lista recibida nula...");
                return;
            }
            if(listFromResponse.isEmpty()){
                mSuccess.postValue(ControlValues.NO_MORE_PLACES_TO_LIST);
                return;
            }
            if (auxList == null){ //Lista vacia se crea
                placeList.postValue(PlaceRepositoryHelper.getListFromResponse(res));
            }
            else{ //Lista creada, se añade la lista recibida a la lista actual
                auxList.addAll(listFromResponse);
                placeList.postValue(auxList);
            }
            mSuccess.postValue(ControlValues.LIST_PLACES_OK);
        }
    }

    public void getPlaceByName(String placeName, String username) {
        String postBodyString = PlaceRepositoryHelper.placeNameJson(placeName, username);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/readLocation");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mPlace.postValue(null);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                String responseString = response.body().string();
                boolean sucess = simpleRequest.isSuccessful(responseString);

                if(sucess){
                    mPlace.postValue(PlaceRepositoryHelper.jsonStringToPlace(responseString));
                }
                else{
                    mPlace.postValue(null);
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
                mSuccess.postValue(ControlValues.ADD_PLACE_FAILED);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                boolean sucess = simpleRequest.isSuccessful(response);
                if(sucess){
                    mSuccess.postValue(ControlValues.ADD_PLACE);
                }
                else{
                    mSuccess.postValue(ControlValues.ADD_PLACE_FAILED);
                }
            }
        });
    }

    public void setPlaceToPendingVisited(TPlace place, String username) {
        String postBodyString = PlaceRepositoryHelper.jsonInfoForFav(place, username);

        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(
                postBodyString,
                AppConstants.METHOD_POST, "/location/newPendingToVisit"
        );
        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mSuccess.postValue(ControlValues.PLACE_TO_PENDING_VISITED_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                boolean success = simpleRequest.isSuccessful(response);
                if(success){
                    mSuccess.postValue(ControlValues.PLACE_TO_PENDING_VISITED_OK);
                }
                else{
                    mSuccess.postValue(ControlValues.PLACE_TO_PENDING_VISITED_FAIL);
                }
            }
        });
    }

    public void modifyPlace(TPlace place, String oldName) throws JSONException {
        String postBodyString = place.json().put("oldName", oldName).toString();

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
                mSuccess.postValue(ControlValues.MODIFY_PLACE_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                boolean sucess = simpleRequest.isSuccessful(response);
                if(sucess){
                    mSuccess.postValue(ControlValues.MODIFY_PLACE_OK);
                }
                else{
                    mSuccess.postValue(ControlValues.MODIFY_PLACE_FAIL);
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
                mSuccess.postValue(ControlValues.DELETE_PLACE_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mSuccess.postValue(ControlValues.DELETE_PLACE_FAIL);
                    throw new IOException("Unexpected code " + response);
                }
                Boolean success = simpleRequest.isSuccessful(response);
                if(success){
                    mSuccess.postValue(ControlValues.DELETE_PLACE_OK);
                }
                else{
                    mSuccess.postValue(ControlValues.DELETE_PLACE_FAIL);
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
                mSuccess.postValue(ControlValues.GET_CATEGORIES_FAIL);
                mCategoriesList.postValue(null);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mSuccess.postValue(ControlValues.GET_CATEGORIES_FAIL);
                    mCategoriesList.postValue(null);
                    throw new IOException("Unexpected code " + response);
                }
                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if (success){
                    mSuccess.postValue(ControlValues.GET_CATEGORIES_OK);//Importante que este despues del postValue de mUser
                    mCategoriesList.postValue(PlaceRepositoryHelper.getCategoriesFromResponse(res));
                }
                else{
                    mSuccess.postValue(ControlValues.GET_CATEGORIES_FAIL);//Importante que este despues del postValue de mUser
                    mCategoriesList.postValue(null);
                }
            }
        });
    }

    public void setFavOnPlace(TPlace place, String username) {

        String postBodyString = PlaceRepositoryHelper.jsonInfoForFav(place, username);

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = null;
        if(place.isUserFav()){
            request = simpleRequest.buildRequest(
                    postBodyString,
                    AppConstants.METHOD_DELETE, "/location/deleteFavoritePlace"
            );
        }
        else{
            request = simpleRequest.buildRequest(
                    postBodyString,
                    AppConstants.METHOD_POST, "/location/newFavoritePlace"
            );
        }

        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mSuccess.postValue(ControlValues.FAV_POST_FAIL);
                mSuccess.postValue(ControlValues.FAV_POST_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mSuccess.postValue(ControlValues.FAV_POST_FAIL);
                    mSuccess.postValue(ControlValues.FAV_POST_FAIL);
                    throw new IOException("Unexpected code " + response);
                }

                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if (success){
                    mSuccess.postValue(ControlValues.FAV_POST_OK);//Importante que este despues del postValue de mUser
                }
                else{
                    mSuccess.postValue(ControlValues.FAV_POST_FAIL);//Importante que este despues del postValue de mUser
                }


            }
        });

    }

    public void setVisitedOnPlace(TPlace place, String username) {

        String postBodyString = PlaceRepositoryHelper.jsonInfoForFav(place, username);

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = null;
        if(!place.getTimeVisited().equals("")){
            request = simpleRequest.buildRequest(
                    postBodyString,
                    AppConstants.METHOD_DELETE, "/location/deletePlaceVisited"
            );
        }
        else{
            request = simpleRequest.buildRequest(
                    postBodyString,
                    AppConstants.METHOD_POST, "/location/newPLaceVisited"
            );
        }

        Call call = simpleRequest.createCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mSuccess.postValue(ControlValues.VISITED_POST_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mSuccess.postValue(ControlValues.VISITED_POST_FAIL);
                    throw new IOException("Unexpected code " + response);
                }

                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if (success){
                    mSuccess.postValue(ControlValues.VISITED_POST_OK);
                }
                else{
                    mSuccess.postValue(ControlValues.VISITED_POST_FAIL);
                }
            }
        });
    }

    public void deletePlacePendingToVisit(String nickname, String placeName){
        JSONObject jsonPlace = new JSONObject();

        try {
            jsonPlace.put("user", nickname);
            jsonPlace.put("location", placeName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String postBodyString = jsonPlace.toString();

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_DELETE, "/location/deletePendingToVisit");

        Call call = simpleRequest.createCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mSuccess.postValue(ControlValues.DELETE_PLACE_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mSuccess.postValue(ControlValues.DELETE_PLACE_FAIL);
                    throw new IOException("Unexpected code " + response);
                }
                Boolean success = simpleRequest.isSuccessful(response);
                if(success){
                    mSuccess.postValue(ControlValues.DELETE_PLACE_OK);
                }
                else{
                    mSuccess.postValue(ControlValues.DELETE_PLACE_FAIL);
                }

            }
        });
    }

    public void listPlaces(int page, int quantity, String nickname, String searchText) {

        String postBodyString = PlaceRepositoryHelper.pageAndQuantToSTring(page, quantity, nickname, searchText);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/listLocations");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new PlaceListCallBack(simpleRequest, mPlacesList));
    }

    public void listPlacesCategories(int page, int quantity, String nickname, String category, String searchText) {

        String postBodyString = PlaceRepositoryHelper.paramsToGetCategoriePlace(page, quantity, nickname, category, searchText);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/listByCategory");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new PlaceListCallBack(simpleRequest, mCategoriesPlacesList));
    }

    public void listFavouritesPlaces(int page, int quantity, String nickname, String searchText) {

        String postBodyString = PlaceRepositoryHelper.pageAndQuantToSTring(page, quantity, nickname, searchText);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/listFavoritesPlaces");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new PlaceListCallBack(simpleRequest, mFavouritesPlacesList));
    }

    public void listTwitterPlaces(int page, int quantity, String nickname, String searchText) {

        String postBodyString = PlaceRepositoryHelper.pageAndQuantToSTring(page, quantity, nickname, searchText);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/listByTwitter");
        Call call = simpleRequest.createCall(request);
        call.enqueue(new PlaceListCallBack(simpleRequest, mTwitterPlacesList));
    }

    public void listNearestPlaces(int page, int quantity, String nickname, List<Double> point, String searchText) {

        //Vaciamos la lista cada vez que se haga una busqueda...
        //Las busqueda por cercano no son paginadas, así que es necesario limpiar el valor
        //en el mutable para no cargar los mismos resultados o resultados no validos.

        if(Looper.myLooper() == Looper.getMainLooper()){ //Si se hace un refresh la llamada no es el thread principal
            mNearestPlacesList.setValue(new ArrayList<>());
        }else{
            mNearestPlacesList.postValue(new ArrayList<>());
        }

        Double longitude = point.get(AppConstants.LONGITUDE);
        Double latitude = point.get(AppConstants.LATITUDE);
        Double radius = AppConstants.DEFAULT_RADIUS;
        Integer nPlaces = AppConstants.DEFAULT_NPLACES;

        //Log.i("PLACE_REPOSITORY", "listNearestPlaces: long: " + longitude + " lat: " + latitude);
        
        String postBodyString = PlaceRepositoryHelper.jsonToSendFrom(longitude, latitude, radius, nPlaces, nickname, searchText);
        
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/listByProximity");
        Call call = simpleRequest.createCall(request);
        call.enqueue(new PlaceListCallBack(simpleRequest, mNearestPlacesList));
    }

    public void listNearestCategories(int page, int quant, String nickname, String category, String searchText, List<Double> point) {

        if(Looper.myLooper() == Looper.getMainLooper()){ //Si se hace un refresh la llamada no es el thread principal
            mCategoriesPlacesList.setValue(new ArrayList<>());
        }else{
            mCategoriesPlacesList.postValue(new ArrayList<>());
        }

        Double longitude = point.get(AppConstants.LONGITUDE);
        Double latitude = point.get(AppConstants.LATITUDE);
        Double radius = AppConstants.DEFAULT_RADIUS;
        Integer nPlaces = AppConstants.DEFAULT_NPLACES;

        String postBodyString = PlaceRepositoryHelper.jsonToSendFrom(longitude, latitude, radius, nPlaces, nickname, searchText, category);

        //Log.d("PLACE_REPO", "PostBody: "+ postBodyString);

        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/listByCategoryAndProximity");
        Call call = simpleRequest.createCall(request);
        call.enqueue(new PlaceListCallBack(simpleRequest, mCategoriesPlacesList));
    }

    public void listVisitedPlaces(int page, int quantity, String nickname, String searchText) {

        String postBodyString = PlaceRepositoryHelper.pageAndQuantToSTring(page, quantity, nickname, searchText);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/listVisitedPLaces");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new PlaceListCallBack(simpleRequest, mPlaceVisited));
    }

    public void listPendingToVisit(int page, int quantity, String nickname, String searchText) {

        String postBodyString = PlaceRepositoryHelper.pageAndQuantToSTring(page, quantity, nickname, searchText);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/listPendingToVisit");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new PlaceListCallBack(simpleRequest, mPlacesPendingToVisit));
    }

    //MutableLiveData Getters
    public MutableLiveData<TPlace> getmPlace() {
        return mPlace;
    }
    public MutableLiveData<List<TPlace>> getPlacesList(){ return mPlacesList; }
    public MutableLiveData<List<TPlace>> getTwitterPlacesList(){ return mTwitterPlacesList; }
    public MutableLiveData<List<String>> getCategoriesList(){ return mCategoriesList; }
    public MutableLiveData<List<TPlace>> getCategoriesPlacesList() { return mCategoriesPlacesList; }
    public MutableLiveData<List<TPlace>> getNearestPlacesList() { return mNearestPlacesList; }
    public MutableLiveData<List<TPlace>> getFavouritesPlacesList() { return mFavouritesPlacesList; }
    public MutableLiveData<List<TPlace>> getPlaceVisitedList() { return mPlaceVisited; }
    public MutableLiveData<List<TPlace>> getPendingToVisitList() { return mPlacesPendingToVisit; }
}