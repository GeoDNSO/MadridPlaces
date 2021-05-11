package com.example.App.ui.place_details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.models.repositories.PlaceRepository;
import com.example.App.models.transfer.TPlace;
import com.example.App.ui.ViewModelParent;

import java.util.List;


public class PlaceDetailViewModel extends ViewModelParent {
    private PlaceRepository placeRepository;
    private MutableLiveData<Boolean> mPlaceDetailActionInProgress = new MutableLiveData<>();
    private LiveData<Integer> mActionPlaceDetailSuccess = new MutableLiveData<>();
    protected LiveData<Integer> mFavSuccess = new MutableLiveData<>();
    protected LiveData<Integer> mVisitedSuccess = new MutableLiveData<>();
    protected LiveData<Integer> mPendingToVisitedSuccess = new MutableLiveData<>();

    public void init(){
        placeRepository = new PlaceRepository();
        mActionPlaceDetailSuccess = Transformations.switchMap(
                placeRepository.getSuccess(),
                success -> setPlaceDetailActionInProgress(success));

        mFavSuccess = Transformations.switchMap(
                placeRepository.getFavSuccess(),
                success -> setFavSuccess(success) //Creo que se puede usar el mismo setSuccess... PROBAR
        );

        mVisitedSuccess = Transformations.switchMap(
                placeRepository.getVisitedSuccess(),
                success -> setFavSuccess(success) //Creo que se puede usar el mismo setSuccess... PROBAR
        );

        mPendingToVisitedSuccess = Transformations.switchMap(
                placeRepository.getmPendingToVisitedSuccess(),
                success -> setFavSuccess(success) //Creo que se puede usar el mismo setSuccess... PROBAR
        );
    }

    public void deletePlace(String placeName){
        mPlaceDetailActionInProgress.setValue(true);
        placeRepository.deletePlace(placeName);
    }

    public void setVisitedOnPlace(TPlace place, String username){
        placeRepository.setVisitedOnPlace(place, username);
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

    public void setFavOnPlace(TPlace place, String username) {
        placeRepository.setFavOnPlace(place, username);
    }

    private LiveData<Integer> setFavSuccess(Integer success) {
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }

    public LiveData<Integer> getFavSuccess(){return mFavSuccess; }
    public LiveData<Integer> getVisitedSuccess(){return mVisitedSuccess; }

    public void placeToPendingVisited(TPlace place, String username) {
        placeRepository.setPlaceToPendingVisited(place, username);
    }

    public LiveData<Integer> getmPendingToVisitedSuccess() {
        return mPendingToVisitedSuccess;
    }
}