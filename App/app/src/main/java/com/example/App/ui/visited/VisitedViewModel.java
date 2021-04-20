package com.example.App.ui.visited;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.App.models.repositories.PlaceRepository;
import com.example.App.models.transfer.TPlace;
import com.example.App.ui.ViewModelParent;

import java.util.List;

public class VisitedViewModel extends ViewModelParent {

    private PlaceRepository placeRepository;

    private MutableLiveData<Boolean> mProgressBar = new MutableLiveData<>(); //true indica progress bar activo
    private LiveData<List<TPlace>> mVisitedList = new MutableLiveData<>();
    private LiveData<List<TPlace>> mPendingToVisitList = new MutableLiveData<>();
    //private LiveData<Integer> mVisitedSuccess = new MutableLiveData<>();


    public void init() {
        placeRepository = new PlaceRepository();
        mSuccess = Transformations.switchMap(
                placeRepository.getSuccess(),
                success -> setSuccess(success)
        );

        mVisitedList = Transformations.switchMap(
                getVisitedPlaces(),
                places -> setAndGetVisitedPlacesList(places)
        );
        mPendingToVisitList = Transformations.switchMap(
                getPendingToVisit(),
                places -> setAndGetPendingToVisitList(places)
        );
    }

    private LiveData<Integer> setSuccess(Integer success) {
        mProgressBar.setValue(false); //progress bar visible
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }
    private LiveData<List<TPlace>> setAndGetVisitedPlacesList(List<TPlace> places) {
        MutableLiveData<List<TPlace>> mAux = new MutableLiveData<>();
        mAux.setValue(places);
        return mAux;
    }
    private LiveData<List<TPlace>> setAndGetPendingToVisitList(List<TPlace> places) {
        MutableLiveData<List<TPlace>> mAux = new MutableLiveData<>();
        mAux.setValue(places);
        return mAux;
    }
    public LiveData<List<TPlace>> getVisitedPlaces() {
        return placeRepository.getPlaceVisitedList();
    }
    public LiveData<List<TPlace>> getPendingToVisit() {
        return placeRepository.getPendingToVisitList();
    }

    public void listVisitedPlaces(int page, int quant, String nickname, String serchText){
        mProgressBar.postValue(true);
        placeRepository.listVisitedPlaces(page, quant, nickname, serchText);
    }
    public void listPendingToVisit(int page, int quant, String nickname, String serchText){
        mProgressBar.postValue(true);
        placeRepository.listPendingToVisit(page, quant, nickname, serchText);
    }
    public void deletePLacePendingToVisit(String nickname, String placeName){
        mProgressBar.postValue(true);
        placeRepository.deletePlacePendingToVisit(nickname, placeName);
    }
}