package com.example.App.models.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.App.models.dao.SimpleRequest;
import com.example.App.models.transfer.TPlace;
import com.example.App.models.transfer.TUser;
import com.example.App.utilities.AppConstants;

import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

public class PlaceRepository extends Repository{

    private MutableLiveData<List<TPlace>> mPlacesList = new MutableLiveData<>();
    private MutableLiveData<TPlace> mPlace = new MutableLiveData<>();

    public void getPlace() {

    }

    public void listPlaces() {
        String postBodyString = "";

        SimpleRequest simpleRequest = new SimpleRequest();

        Request request = simpleRequest.buildRequest(postBodyString,
                AppConstants.METHOD_POST, "/listUsers/");

        Call call = simpleRequest.createCall(request);
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
}
