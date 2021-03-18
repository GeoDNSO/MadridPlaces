package com.example.App.ui.place_details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.models.repositories.PlaceRepository;


public class PlaceDetailViewModel extends ViewModel {
    private PlaceRepository placeRepository;
    private MutableLiveData<Boolean> mPlaceDetailActionInProgress = new MutableLiveData<>();
    private LiveData<Integer> mActionPlaceDetailSuccess = new MutableLiveData<>();

    public void init(){
        placeRepository = new PlaceRepository();
        mActionPlaceDetailSuccess = Transformations.switchMap(
                placeRepository.getSuccess(),
                success -> setPlaceDetailActionInProgress(success));
    }

    public void deletePlace(String placeName){
        mPlaceDetailActionInProgress.setValue(true);
        placeRepository.deletePlace(placeName);
    }

    private LiveData<Integer> setPlaceDetailActionInProgress(Integer success) {
        mPlaceDetailActionInProgress.setValue(false); //progress bar visible
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }

    public LiveData<Boolean> getPlaceDetailActionInProgress(){
        return mPlaceDetailActionInProgress;
    }

    public LiveData<Integer> getPlaceDetailProfileSuccess(){
        return mActionPlaceDetailSuccess;
    }
}