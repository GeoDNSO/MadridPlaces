package com.example.App.models.repositories;

import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.App.App;
import com.example.App.models.dao.SimpleRequest;
import com.example.App.models.transfer.TPlace;
import com.example.App.models.transfer.TUser;
import com.example.App.services.LocationTrack;
import com.example.App.utilities.AppConstants;
import com.mapbox.geojson.CoordinateContainer;
import com.mapbox.geojson.Point;

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
    private MutableLiveData<List<TPlace>> mTwitterPlacesList = new MutableLiveData<>();
    private MutableLiveData<List<TPlace>> mHistoryPlacesList = new MutableLiveData<>();
    private MutableLiveData<List<TPlace>> mNearestPlacesList = new MutableLiveData<>();
    private MutableLiveData<List<TPlace>> mFavouritesPlacesList = new MutableLiveData<>();
    private MutableLiveData<List<TPlace>> mCategoriesPlacesList = new MutableLiveData<>();
    private MutableLiveData<List<String>> mCategoriesList = new MutableLiveData<>();
    private MutableLiveData<TPlace> mPlace = new MutableLiveData<>();
    private MutableLiveData<List<TPlace>> mPlaceVisited = new MutableLiveData<>();
    private MutableLiveData<List<TPlace>> mPlacesPendingToVisit = new MutableLiveData<>();

    private MutableLiveData<Integer> mFavSuccess = new MutableLiveData<Integer>();
    private MutableLiveData<Integer> mVisitedSuccess = new MutableLiveData<Integer>();

    public LiveData<Boolean> getBooleanPlace(){ return mBooleanPlace; }
    public LiveData<List<TPlace>> getPlacesList(){ return mPlacesList; }
    public LiveData<List<TPlace>> getTwitterPlacesList(){ return mTwitterPlacesList; }
    public LiveData<List<TPlace>> getHistoryPlacesList(){ return mHistoryPlacesList; }
    public LiveData<List<String>> getCategoriesList(){ return mCategoriesList; }
    public MutableLiveData<List<TPlace>> getCategoriesPlacesList() { return mCategoriesPlacesList; }
    public MutableLiveData<List<TPlace>> getNearestPlacesList() { return mNearestPlacesList; }
    public MutableLiveData<List<TPlace>> getFavouritesPlacesList() { return mFavouritesPlacesList; }
    public MutableLiveData<List<TPlace>> getPlaceVisitedList() { return mPlaceVisited; }
    public MutableLiveData<List<TPlace>> getPendingToVisitList() { return mPlacesPendingToVisit; }


    public MutableLiveData<Integer> getFavSuccess() { return mFavSuccess; }
    public MutableLiveData<Integer> getVisitedSuccess() { return mVisitedSuccess; }


    //Callback personalizado tanto para List como para Append
    class PlaceListCallBack implements Callback{

        private SimpleRequest simpleRequest;
        private MutableLiveData<List<TPlace>> placeList;


        public PlaceListCallBack(SimpleRequest simpleRequest, MutableLiveData<List<TPlace>> placeList){
            this.simpleRequest = simpleRequest;
            this.placeList = placeList;
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
            Log.d("PLACE_REPOSITORY", "FAILURE, error above");
            mSuccess.postValue(AppConstants.ERROR_LIST_PLACES);
            placeList.postValue(null);
            call.cancel();
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            this.sleep(500);//Para simular la carga...

            if (!response.isSuccessful()) {
                mSuccess.postValue(AppConstants.ERROR_LIST_PLACES);
                throw new IOException("Unexpected code " + response);
            }
            String res = response.body().string();
            boolean success = simpleRequest.isSuccessful(res);

            if(!success){
                Log.d("PlaceListCallback", "Not success");
                placeList.postValue(null);
                mSuccess.postValue(AppConstants.ERROR_LIST_PLACES);//Importante que este despues del postValue de mUser

                return;
            }
            //si no hubo problemas...
            List<TPlace> listaAux = placeList.getValue();
            List<TPlace> listaFromResponse = getListFromResponse(res);
            if(listaFromResponse == null){
                Log.d("PLACE_REPO", "La lista JSON convertida es NULO, MIRAR...");
                return;
            }
            if(listaFromResponse.isEmpty()){
                return;
            }
            if (listaAux == null){
                placeList.postValue(getListFromResponse(res));
            }
            else{
                listaAux.addAll(listaFromResponse);
                placeList.postValue(listaAux);
            }
            mSuccess.postValue(AppConstants.LIST_PLACES);//Importante que este despues del postValue de mUser
        }
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

    public void modifyPlace(TPlace place, String oldName) throws JSONException {
        String postBodyString = place.json().put("oldName", oldName).toString();

        Log.d ("aaa", postBodyString);
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

    public void setFavOnPlace(TPlace place, String username) {

        String postBodyString = jsonInfoForFav(place, username);

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
                mFavSuccess.postValue(AppConstants.FAV_POST_FAIL);
                mSuccess.postValue(AppConstants.FAV_POST_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mFavSuccess.postValue(AppConstants.FAV_POST_FAIL);
                    mSuccess.postValue(AppConstants.FAV_POST_FAIL);
                    throw new IOException("Unexpected code " + response);
                }

                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if (success){
                    mFavSuccess.postValue(AppConstants.FAV_POST_OK);//Importante que este despues del postValue de mUser
                }
                else{
                    mFavSuccess.postValue(AppConstants.FAV_POST_FAIL);//Importante que este despues del postValue de mUser
                }


            }
        });

    }

    //lista lugares de quantity en quantity en función de page alfabeticamente
    // Ej: quantity = 100 -> (page:0 = 1-100, page:1 = 101-200...)
    public void listPlaces(int page, int quantity, String nickname, String searchText) {

        String postBodyString = pageAndQuantToSTring(page, quantity, nickname, searchText);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/listLocations");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new PlaceListCallBack(simpleRequest, mPlacesList));
    }

    public void listPlacesCategories(int page, int quantity, String nickname, String category, String searchText) {

        String postBodyString = paramsToGetCategoriePlace(page, quantity, nickname, category, searchText);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/listByCategory");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new PlaceListCallBack(simpleRequest, mCategoriesPlacesList));
    }

    public void listFavouritesPlaces(int page, int quantity, String nickname, String searchText) {

        String postBodyString = pageAndQuantToSTring(page, quantity, nickname, searchText);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/listFavoritesPlaces");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new PlaceListCallBack(simpleRequest, mFavouritesPlacesList));
    }

    public void listTwitterPlaces(int page, int quantity, String nickname, String searchText) {

        String postBodyString = pageAndQuantToSTring(page, quantity, nickname, searchText);
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
        
        String postBodyString = jsonToSendFrom(longitude, latitude, radius, nPlaces, nickname, searchText);
        
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

        String postBodyString = jsonToSendFrom(longitude, latitude, radius, nPlaces, nickname, searchText, category);

        Log.d("PLACE_REPO", "PostBody: "+ postBodyString);

        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/listByCategoryAndProximity");
        Call call = simpleRequest.createCall(request);
        call.enqueue(new PlaceListCallBack(simpleRequest, mCategoriesPlacesList));
    }


    public void listVisitedPlaces(int page, int quantity, String nickname, String searchText) {

        String postBodyString = pageAndQuantToSTring(page, quantity, nickname, searchText);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/listVisitedPLaces");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new PlaceListCallBack(simpleRequest, mPlaceVisited));
    }
    public void listPendingToVisit(int page, int quantity, String nickname, String searchText) {

        String postBodyString = pageAndQuantToSTring(page, quantity, nickname, searchText);
        SimpleRequest simpleRequest = new SimpleRequest();
        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/location/listPendingToVisit");
        Call call = simpleRequest.createCall(request);

        call.enqueue(new PlaceListCallBack(simpleRequest, mPlacesPendingToVisit));
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

    public void setVisitedOnPlace(TPlace place, String username) {

        String postBodyString = jsonInfoForFav(place, username);

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
                mVisitedSuccess.postValue(AppConstants.VISITED_POST_FAIL);
                mSuccess.postValue(AppConstants.VISITED_POST_FAIL);
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mVisitedSuccess.postValue(AppConstants.VISITED_POST_FAIL);
                    mSuccess.postValue(AppConstants.VISITED_POST_FAIL);
                    throw new IOException("Unexpected code " + response);
                }

                String res = response.body().string();
                boolean success = simpleRequest.isSuccessful(res);

                if (success){
                    mVisitedSuccess.postValue(AppConstants.VISITED_POST_OK);//Importante que este despues del postValue de mUser
                }
                else{
                    mVisitedSuccess.postValue(AppConstants.VISITED_POST_FAIL);//Importante que este despues del postValue de mUser
                }
            }
        });
    }

    //Utilidades JSON

    private String jsonToSendFrom(Double longitude, Double latitude, Double radius, Integer nPlaces, String nickname, String searchText, String category) {
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


    private String jsonToSendFrom(Double longitude, Double latitude, Double radius, Integer nPlaces, String nickname, String search) {

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

    private String pageAndQuantToSTring(int page, int quantity, String nickname, String search) {
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

    private String paramsToGetCategoriePlace(int page, int quantity, String nickname, String category, String search) {
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

    public String jsonInfoForFav(TPlace place, String username){

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