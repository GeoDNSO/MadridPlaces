package com.example.App.ui.categories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.App.repositories.PlaceRepository;
import com.example.App.components.ViewModelParent;

import java.util.List;

public class CategoriesViewModel extends ViewModelParent {


    private PlaceRepository placeRepository;
    private LiveData<List<String>> mCategoriesStringList = new MutableLiveData<>();

    //observamos los objetos del repositorio, en este caso, el success devuelto por la llamada okhttp
    public void init(){
        placeRepository = new PlaceRepository();

        mSuccess = super.updateOnChange(mSuccess, placeRepository.getSuccess());
        mCategoriesStringList = super.updateOnChange(mCategoriesStringList, placeRepository.getCategoriesList());

    }

    public void getTypesOfPlaces(){
        mlv_isLoading.setValue(true); //progress bar visible
        placeRepository.getCategories();
    }

    public LiveData<List<String>> getmCategoriesStringList(){
        return mCategoriesStringList;
    }

}