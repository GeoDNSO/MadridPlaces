package com.example.App.ui.add_place;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.App.repositories.PlaceRepository;
import com.example.App.models.TPlace;
import com.example.App.components.ViewModelParent;

import java.util.List;

public class AddPlaceViewModel extends ViewModelParent {

    private PlaceRepository placeRepository;
    private LiveData<List<String>> mCategoriesList = new MutableLiveData<>();

    //observamos los objetos del repositorio, en este caso, el success devuelto por la llamada okhttp
    public void init(){
        placeRepository = new PlaceRepository();

        mSuccess = super.updateOnChange(placeRepository.getSuccess());
        mCategoriesList = super.updateOnChange(placeRepository.getCategoriesList());

    }

    public void getTypesOfPlaces(){
        placeRepository.getCategories();
    }

    public void addPlace(String placeName, String placeDescription, String typePlace, List<String> listImages, Double latitude, Double longitude,
                         String road_class, String road_name, String road_number, String zipcode){
        TPlace place = new TPlace(placeName, placeDescription, latitude, longitude, listImages, typePlace, "Madrid",
                road_class, road_name, road_number, zipcode, "", 0.0, false, 100.0, 0, "Sin Fecha");
        //TODO en type of place no devolvemos el nombre del lugar sino el numero asignado en la base de datos
        placeRepository.addPlace(place);
    }

    public LiveData<List<String>> getmCategoriesList(){
        return mCategoriesList;
    }
}
