package com.example.App.ui.places_list.subclasses.rating;

import androidx.lifecycle.ViewModelProvider;

import com.example.App.App;
import com.example.App.ui.places_list.BasePlaces;
import com.example.App.ui.places_list.BaseViewModel;

import java.util.ArrayList;

public class RatingPlacesFragment extends BasePlaces {

    public RatingPlacesFragment(){
        super();
        placeList = new ArrayList<>();
    }
    @Override
    public void listPlaces() {
        super.mViewModel.listPlaces(page, quantum, App.getInstance().getUsername(), search_text);
    }

    @Override
    public BaseViewModel getViewModelToParent() {
        RatingPlaceViewModel rvm = new ViewModelProvider(this).get(RatingPlaceViewModel.class);
        return rvm;
    }
}
