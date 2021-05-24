package com.example.App.ui.places_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.App.repositories.PlaceRepository;
import com.example.App.models.TPlace;
import com.example.App.components.ViewModelParent;

import java.util.List;

public abstract class BaseViewModel extends ViewModelParent {
    protected PlaceRepository placeRepository;
    protected LiveData<List<TPlace>> mPlacesList = new MutableLiveData<>();

    protected abstract MutableLiveData<List<TPlace>> getPlaceListToParent();
    public abstract void listPlaceToParent(int page, int quant, String nickname, String searchText);

    @Override
    public void init() {
        placeRepository = new PlaceRepository();

        mSuccess = super.updateOnChange(placeRepository.getSuccess());
        mPlacesList = super.updateOnChange(getPlaceListToParent());
    }
    
    public void listPlaces(int page, int quant, String nickname, String serchText){
        listPlaceToParent(page, quant, nickname, serchText);
    }

    public void setFavOnPlace(TPlace place, String username) {
        placeRepository.setFavOnPlace(place, username);
    }

    public LiveData<List<TPlace>> getPlacesList(){ return mPlacesList; }

}
