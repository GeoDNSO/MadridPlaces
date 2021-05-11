package com.example.App.ui.places_list.subclasses;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.App.repositories.PlaceRepository;
import com.example.App.models.TPlace;
import com.example.App.ui.ViewModelParent;

import java.util.List;

public abstract class BaseViewModel extends ViewModelParent {
    protected PlaceRepository placeRepository;
    protected LiveData<List<TPlace>> mPlacesList = new MutableLiveData<>();
    protected LiveData<Integer> mFavSuccess = new MutableLiveData<>();

    @Override
    public void init() {
        placeRepository = new PlaceRepository();
        mSuccess = Transformations.switchMap(
                placeRepository.getSuccess(),
                success -> setSuccess(success)
        );

        mPlacesList = Transformations.switchMap(
                getPlaceListToParent(),
                places -> setAndGetPlacesList(places)
        );

        mFavSuccess = Transformations.switchMap(
                placeRepository.getFavSuccess(),
                success -> setFavSuccess(success) //Creo que se puede usar el mismo setSuccess... PROBAR
        );

    }



    protected abstract LiveData<List<TPlace>> getPlaceListToParent();

    public abstract void listPlaceToParent(int page, int quant, String nickname, String serchText);


    //Peticion de quant lugares de la pagina page al servidor
    public void listPlaces(int page, int quant, String nickname, String serchText){
        mProgressBar.postValue(true);
        listPlaceToParent(page, quant, nickname, serchText);
    }

    private LiveData<List<TPlace>> setAndGetPlacesList(List<TPlace> places) {
        MutableLiveData<List<TPlace>> mAux = new MutableLiveData<>();
        mAux.setValue(places);
        return mAux;
    }

    private LiveData<Integer> setSuccess(Integer success) {
        mProgressBar.setValue(false); //progress bar visible
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }

    private LiveData<Integer> setFavSuccess(Integer success) {
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }

    public LiveData<List<TPlace>> getPlacesList(){ return mPlacesList; }

    public LiveData<Integer> getFavSuccess(){return mFavSuccess; }

    public void setFavOnPlace(TPlace place, String username) {
        placeRepository.setFavOnPlace(place, username);
    }
}
