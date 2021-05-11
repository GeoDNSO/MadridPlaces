package com.example.App.ui.categories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.App.repositories.PlaceRepository;
import com.example.App.components.ViewModelParent;

import java.util.ArrayList;
import java.util.List;

public class CategoriesViewModel extends ViewModelParent {


    private PlaceRepository placeRepository;
    private List<String> mListTypesOfPlace;
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

        mCategoriesSuccess = Transformations.switchMap(
                placeRepository.getCategoriesList(),
                success -> setAndGetCategoriesPlace(success));
    }

    private LiveData<Integer> setSuccess(Integer success) {
        mlv_isLoading.setValue(false); //progress bar visible
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }


    public void getTypesOfPlaces(){
        mlv_isLoading.setValue(true); //progress bar visible
        placeRepository.getCategories();
    }

    private LiveData<List<String>> setAndGetCategoriesPlace(List<String> categories){
        mlv_isLoading.setValue(false); //progress bar visible
        MutableLiveData<List<String>> mAux = new MutableLiveData<>();
        mAux.setValue(categories);
        return mAux;
    }


    public LiveData<List<String>> getmCategoriesSuccess(){
        return mCategoriesSuccess;
    }

}