package com.example.App.ui.places_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.App.models.repositories.PlaceRepository;
import com.example.App.models.transfer.TPlace;
import com.example.App.ui.ViewModelParent;

import java.util.List;

public class PlacesListViewModel extends ViewModelParent {
    private PlaceRepository placeRepository;
    private LiveData<List<TPlace>> mPlacesList = new MutableLiveData<>();

    @Override
    public void init() {
        placeRepository = new PlaceRepository();
        mSuccess = Transformations.switchMap(
                placeRepository.getSuccess(),
                success -> setSuccess(success)
        );

        mPlacesList = Transformations.switchMap(
                placeRepository.getPlacesList(),
                places -> setAndGetPlacesList(places));

        mPlacesList = Transformations.switchMap(
                placeRepository.getCategoriesPlacesList(),
                places -> setAndGetPlacesList(places));


    }

    //Peticion de quant lugares de la pagina page al servidor
    public void listPlaces(int page, int quant){
        mProgressBar.postValue(true);
        placeRepository.listPlaces(page, quant);
    }

    //Peticion de quant lugares de la pagina page al servidor añadiendo anteriores
    public void appendPlaces(int page, int quant){
        mProgressBar.postValue(true);
        placeRepository.appendPlaces(page, quant);
    }

    //Peticion de quant lugares de la pagina page al servidor
    public void listPlaces(int page, int quant, String category){
        mProgressBar.postValue(true);
        placeRepository.listPlacesCategories(page, quant, category);
    }

    //Peticion de quant lugares de la pagina page al servidor añadiendo anteriores
    public void appendPlaces(int page, int quant, String category){
        mProgressBar.postValue(true);
        placeRepository.appendPlacesCategories(page, quant, category);
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

    public LiveData<List<TPlace>> getPlacesList(){ return mPlacesList; }
}