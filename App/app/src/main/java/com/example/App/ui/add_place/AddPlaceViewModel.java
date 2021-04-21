package com.example.App.ui.add_place;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.models.repositories.PlaceRepository;
import com.example.App.models.repositories.UserRepository;
import com.example.App.models.transfer.TPlace;
import com.example.App.models.transfer.TUser;
import com.example.App.ui.ViewModelParent;

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
        mSuccess = Transformations.switchMap(
                placeRepository.getSuccess(),
                success -> setSuccess(success)
        );
        mAddPlaceSuccess = Transformations.switchMap(
                placeRepository.getBooleanPlace(),
                success -> setAndGetAddPlace(success));

        mCategoriesSuccess = Transformations.switchMap(
                placeRepository.getCategoriesList(),
                success -> setAndGetCategoriesPlace(success));
    }

    private LiveData<Boolean> setAndGetAddPlace(Boolean success) {
        MutableLiveData<Boolean> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }

    private LiveData<Integer> setSuccess(Integer success) {
        mProgressBar.setValue(false); //progress bar visible
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }

    public void getTypesOfPlaces(){
        mProgressBar.setValue(true); //progress bar visible
        placeRepository.getCategories();
    }

    private LiveData<List<String>> setAndGetCategoriesPlace(List<String> categories){
        mProgressBar.setValue(false); //progress bar visible
        MutableLiveData<List<String>> mAux = new MutableLiveData<>();
        mAux.setValue(categories);
        return mAux;
    }

    public void addPlace(String placeName, String placeDescription, String typePlace, List<String> listImages, Double latitude, Double longitude,
                         String road_class, String road_name, String road_number, String zipcode){
        mProgressBar.setValue(true); //progress bar visible
        TPlace place = new TPlace(placeName, placeDescription, latitude, longitude, listImages, typePlace, "Madrid",
                road_class, road_name, road_number, zipcode, "", 0.0, false, 100.0, 50, "Sin Fecha");
        //TODO en type of place no devolvemos elnombre del lugar sino el numero asignado en la base de datos
        placeRepository.addPlace(place);
        //placeRepository.addPlace(placeName, placeDescription, typePlace);
        //placeRepository.addPlaceImages(placeName, listImages);

    }

    public LiveData<Boolean> getmAddPlaceSuccess(){
        return mAddPlaceSuccess;
    }
    public LiveData<List<String>> getmCategoriesSuccess(){
        return mCategoriesSuccess;
    }
}
