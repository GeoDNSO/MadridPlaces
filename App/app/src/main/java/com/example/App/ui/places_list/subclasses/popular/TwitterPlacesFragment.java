package com.example.App.ui.places_list.subclasses.popular;

import androidx.lifecycle.ViewModelProvider;

import com.example.App.App;
import com.example.App.ui.places_list.BasePlaces;
import com.example.App.ui.places_list.BaseViewModel;

import java.util.ArrayList;

public class TwitterPlacesFragment extends BasePlaces{

    //Cambiar llamadas a viewmodel segun el tipo de lugares que se busque
    public TwitterPlacesFragment(){
        super();
        placeList = new ArrayList<>();
    }
    @Override
    public void listPlaces() {
        super.mViewModel.listPlaces(page, quantum, App.getInstance().getUsername(), search_text);
    }

    @Override
    public BaseViewModel getViewModelToParent() {
        TwitterPlaceViewModel tvm = new ViewModelProvider(this).get(TwitterPlaceViewModel.class);
        return tvm;
    }
}
