package com.example.App.ui.place_details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.App.App;
import com.example.App.repositories.PlaceRepository;
import com.example.App.models.TPlace;
import com.example.App.components.ViewModelParent;


public class PlaceDetailViewModel extends ViewModelParent {
    private PlaceRepository placeRepository;

    private LiveData<TPlace> mPlace = new MutableLiveData<>();
    private MutableLiveData<Boolean> mPlaceDetailActionInProgress = new MutableLiveData<>();

    public void init(){
        placeRepository = new PlaceRepository();

        mSuccess =  super.updateOnChange(mSuccess, placeRepository.getSuccess());
        mPlace = super.updateOnChange(mPlace, placeRepository.getmPlace());
    }

    public void deletePlace(String placeName){
        mPlaceDetailActionInProgress.setValue(true);
        placeRepository.deletePlace(placeName);
    }

    public void setVisitedOnPlace(TPlace place, String username){
        placeRepository.setVisitedOnPlace(place, username);
    }

    public void setFavOnPlace(TPlace place, String username) {
        placeRepository.setFavOnPlace(place, username);
    }

    public void placeToPendingVisited(TPlace place, String username) {
        placeRepository.setPlaceToPendingVisited(place, username);
    }

    public void getPlaceByName(String bundlePlaceName) {
        placeRepository.getPlaceByName(bundlePlaceName, App.getInstance().getUsername());
    }

    public LiveData<TPlace> getmPlace() {
        return mPlace;

    }
}