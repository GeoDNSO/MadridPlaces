package com.example.App.ui.recommendations;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class RecommendationsViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    List<String> mListTypesOfPlace = new ArrayList<String>();

    public List<String> getTypesOfPlaces(){
        mListTypesOfPlace.add("hola");
        mListTypesOfPlace.add("adios");

        return mListTypesOfPlace;
    }
}