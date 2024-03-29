package com.example.App.ui.visited.subclasses.visitedPlaces;

import androidx.lifecycle.ViewModelProvider;

import com.example.App.App;
import com.example.App.ui.places_list.BasePlaces;
import com.example.App.ui.places_list.BaseViewModel;

import java.util.ArrayList;

public class VisitedPlacesFragment extends BasePlaces {

    public VisitedPlacesFragment(){
        super();
        placeList = new ArrayList<>();
    }
    @Override
    public void listPlaces() {
        super.mViewModel.listPlaces(page, quantum, App.getInstance().getUsername(), search_text);
    }

    @Override
    public BaseViewModel getViewModelToParent() {
        VisitedPlacesViewModel rvm = new ViewModelProvider(this).get(VisitedPlacesViewModel.class);
        return rvm;
    }
}