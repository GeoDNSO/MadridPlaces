package com.example.App.ui.modify_place;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.models.repositories.PlaceRepository;
import com.example.App.models.transfer.TPlace;
import com.example.App.ui.ViewModelParent;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ModifyPlaceViewModel extends ViewModelParent {
    private PlaceRepository placeRepository;
    List<String> mListTypesOfPlace;
    private LiveData<Boolean> mModifyPlaceSuccess = new MutableLiveData<>();
    private LiveData<List<String>> mCategoriesSuccess = new MutableLiveData<>();

    public void init(){
        placeRepository = new PlaceRepository();
        mListTypesOfPlace = new ArrayList<>();
        mSuccess = Transformations.switchMap(
                placeRepository.getSuccess(),
                success -> setSuccess(success)
        );
        mModifyPlaceSuccess = Transformations.switchMap(
                placeRepository.getBooleanPlace(),
                success -> setAndGetModifyPlace(success));

        mCategoriesSuccess = Transformations.switchMap(
                placeRepository.getCategoriesList(),
                success -> setAndGetCategoriesPlace(success));
    }

    public void getTypesOfPlaces(){
        mProgressBar.setValue(true); //progress bar visible
        placeRepository.getCategories();
    }

    public void modifyPlace(String placeName, String placeDescription, String typePlace, List<String> listImages, TPlace p) throws JSONException {
        mProgressBar.setValue(true); //progress bar visible
        TPlace place = new TPlace(placeName, placeDescription, p.getLatitude(), p.getLongitude(), listImages, typePlace, p.getCity(),
<<<<<<< HEAD
                p.getRoad_class(), p.getRoad_name(), p.getRoad_number(), p.getZipcode(), p.getAffluence(), p.getRating(), p.isUserFav(), 100.0, 100);
        //TODO en type of place no devolvemos elnombre del lugar sino el numero asignado en la base de datos
=======
                p.getRoad_class(), p.getRoad_name(), p.getRoad_number(), p.getZipcode(), p.getAffluence(), p.getRating(), p.isUserFav());
>>>>>>> 19ba3443dfd8f073c3a8554326ea9f2c920bd0e0
        placeRepository.modifyPlace(place, p.getName());

    }

    public void modifyPlace(String placeName, String placeDescription, String typePlace, TPlace p) throws JSONException {
        mProgressBar.setValue(true); //progress bar visible
        TPlace place = new TPlace(placeName, placeDescription, p.getLatitude(), p.getLongitude(), p.getImagesList(), typePlace, p.getCity(),
<<<<<<< HEAD
                p.getRoad_class(), p.getRoad_name(), p.getRoad_number(), p.getZipcode(), p.getAffluence(), p.getRating(), p.isUserFav(), 100.0, 100);
        //TODO en type of place no devolvemos elnombre del lugar sino el numero asignado en la base de datos
=======
                p.getRoad_class(), p.getRoad_name(), p.getRoad_number(), p.getZipcode(), p.getAffluence(), p.getRating(), p.isUserFav());
>>>>>>> 19ba3443dfd8f073c3a8554326ea9f2c920bd0e0
        placeRepository.modifyPlace(place, p.getName());
    }

    private LiveData<Boolean> setAndGetModifyPlace(Boolean success) {
        MutableLiveData<Boolean> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }

    private LiveData<List<String>> setAndGetCategoriesPlace(List<String> categories){
        mProgressBar.setValue(false); //progress bar visible
        MutableLiveData<List<String>> mAux = new MutableLiveData<>();
        mAux.setValue(categories);
        return mAux;
    }

    private LiveData<Integer> setSuccess(Integer success) {
        mProgressBar.setValue(false); //progress bar visible
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }

    public LiveData<Boolean> getmModifyPlaceSuccess(){
        return mModifyPlaceSuccess;
    }
    public LiveData<List<String>> getmCategoriesSuccess(){
        return mCategoriesSuccess;
    }

}
