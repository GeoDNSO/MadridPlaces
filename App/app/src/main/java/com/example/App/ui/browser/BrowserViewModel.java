package com.example.App.ui.browser;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.App.models.transfer.TUser;

import java.util.ArrayList;
import java.util.List;

public class BrowserViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    List<String> mListTypesOfPlace = new ArrayList<String>();

    public List<String> getTypesOfPlaces(){
        mListTypesOfPlace.add("hola");
        mListTypesOfPlace.add("adios");

        return mListTypesOfPlace;
    }
}