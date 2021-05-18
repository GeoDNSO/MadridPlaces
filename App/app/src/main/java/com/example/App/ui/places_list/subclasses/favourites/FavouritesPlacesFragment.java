package com.example.App.ui.places_list.subclasses.favourites;

import androidx.lifecycle.ViewModelProvider;

import com.example.App.App;
import com.example.App.ui.places_list.subclasses.BasePlaces;
import com.example.App.ui.places_list.subclasses.BaseViewModel;

import java.util.ArrayList;

public class FavouritesPlacesFragment extends BasePlaces {

    public FavouritesPlacesFragment(){
        super();
        placeList = new ArrayList<>();
    }

    @Override
    public void listPlaces() {
        super.mViewModel.listPlaces(page, quantum, App.getInstance().getUsername(), search_text);
    }

    @Override
    public BaseViewModel getViewModelToParent() {
        FavouritesPlacesViewModel rvm = new ViewModelProvider(this).get(FavouritesPlacesViewModel.class);
        return rvm;
    }
}
