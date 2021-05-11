package com.example.App.ui.add_place;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.App.repositories.PlaceRepository;
import com.example.App.models.TPlace;
import com.example.App.components.ViewModelParent;

import java.util.ArrayList;
import java.util.List;

public class AddPlaceViewModel extends ViewModelParent {

    private PlaceRepository placeRepository;
    List<String> mListTypesOfPlace;
    private LiveData<Boolean> mAddPlaceSuccess = new MutableLiveData<>();
    private LiveData<List<String>> mCategoriesSuccess = new MutableLiveData<>();

    //observamos los objetos del repositorio, en este caso, el success devuelto por la llamada okhttp
    public void init(){
        placeRepository = new PlaceRepository();
        mListTypesOfPlace = new ArrayList<>();

        mSuccess = super.updateOnChange(mSuccess, placeRepository.getSuccess());
        mAddPlaceSuccess = super.updateOnChange(mAddPlaceSuccess, placeRepository.getBooleanPlace());
        mCategoriesSuccess = super.updateOnChange(mCategoriesSuccess, placeRepository.getCategoriesList());

    }

    public void getTypesOfPlaces(){
        mlv_isLoading.setValue(true); //progress bar visible
        placeRepository.getCategories();
    }

    public void addPlace(String placeName, String placeDescription, String typePlace, List<String> listImages, Double latitude, Double longitude,
                         String road_class, String road_name, String road_number, String zipcode){
        mlv_isLoading.setValue(true); //progress bar visible
        TPlace place = new TPlace(placeName, placeDescription, latitude, longitude, listImages, typePlace, "Madrid",
                road_class, road_name, road_number, zipcode, "", 0.0, false, 100.0, 0, "Sin Fecha");
        //TODO en type of place no devolvemos el nombre del lugar sino el numero asignado en la base de datos
        placeRepository.addPlace(place);
    }

    public LiveData<Boolean> getmAddPlaceSuccess(){
        return mAddPlaceSuccess;
    }
    public LiveData<List<String>> getmCategoriesSuccess(){
        return mCategoriesSuccess;
    }
}
