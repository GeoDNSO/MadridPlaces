package com.example.App.ui.modify_place;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.App.repositories.PlaceRepository;
import com.example.App.models.TPlace;
import com.example.App.components.ViewModelParent;

import org.json.JSONException;

import java.util.List;

public class ModifyPlaceViewModel extends ViewModelParent {
    private PlaceRepository placeRepository;
    private LiveData<List<String>> mCategoriesList = new MutableLiveData<>();

    public void init(){
        placeRepository = new PlaceRepository();

        mSuccess = super.updateOnChange(placeRepository.getSuccess());
        mCategoriesList = super.updateOnChange(placeRepository.getCategoriesList());
    }

    public void getTypesOfPlaces(){
        placeRepository.getCategories();
    }

    public void modifyPlace(String placeName, String placeDescription, String typePlace, List<String> listImages, TPlace p, Double latitude, Double longitude,
                            String road_class, String road_name, String road_number, String zipcode) throws JSONException {
        TPlace place = new TPlace(placeName, placeDescription, latitude, longitude, listImages, typePlace, p.getCity(),
                road_class, road_name, road_number, zipcode, p.getAffluence(), p.getRating(), p.isUserFav(), 100.0, 100, "Sin Fecha");
        //TODO en type of place no devolvemos elnombre del lugar sino el numero asignado en la base de datos
        placeRepository.modifyPlace(place, p.getName());

    }

    public void modifyPlace(String placeName, String placeDescription, String typePlace, TPlace p, Double latitude, Double longitude,
                            String road_class, String road_name, String road_number, String zipcode) throws JSONException {
        TPlace place = new TPlace(placeName, placeDescription, latitude, longitude, p.getImagesList(), typePlace, p.getCity(),
                road_class, road_name, road_number, zipcode, p.getAffluence(), p.getRating(), p.isUserFav(), 100.0, 100, "Sin Fecha Modificado");
        //TODO en type of place no devolvemos elnombre del lugar sino el numero asignado en la base de datos
        placeRepository.modifyPlace(place, p.getName());
    }

    public LiveData<List<String>> getmCategoriesList(){
        return mCategoriesList;
    }

}
