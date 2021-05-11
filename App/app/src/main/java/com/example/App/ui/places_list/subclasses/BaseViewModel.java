package com.example.App.ui.places_list.subclasses;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.App.repositories.PlaceRepository;
import com.example.App.models.TPlace;
import com.example.App.components.ViewModelParent;

import java.util.List;

public abstract class BaseViewModel extends ViewModelParent {
    protected PlaceRepository placeRepository;
    protected LiveData<List<TPlace>> mPlacesList = new MutableLiveData<>();
    protected LiveData<Integer> mFavSuccess = new MutableLiveData<>();

    protected abstract MutableLiveData<List<TPlace>> getPlaceListToParent();
    public abstract void listPlaceToParent(int page, int quant, String nickname, String serchText);

    @Override
    public void init() {
        placeRepository = new PlaceRepository();

        mSuccess = super.updateOnChange(mSuccess, placeRepository.getSuccess());
        mPlacesList = super.updateOnChange(mPlacesList, getPlaceListToParent());
        mFavSuccess = super.updateOnChange(mFavSuccess, placeRepository.getFavSuccess());
    }
    
    public void listPlaces(int page, int quant, String nickname, String serchText){
        mlv_isLoading.postValue(true);
        listPlaceToParent(page, quant, nickname, serchText);
    }

    public void setFavOnPlace(TPlace place, String username) {
        placeRepository.setFavOnPlace(place, username);
    }

    public LiveData<List<TPlace>> getPlacesList(){ return mPlacesList; }

    public LiveData<Integer> getFavSuccess(){return mFavSuccess; }
}
