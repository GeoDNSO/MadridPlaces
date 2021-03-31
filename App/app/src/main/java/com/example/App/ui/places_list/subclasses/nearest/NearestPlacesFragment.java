package com.example.App.ui.places_list.subclasses.nearest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.App.ui.places_list.subclasses.BasePlaces;
import com.example.App.ui.places_list.subclasses.BaseViewModel;
import com.example.App.ui.places_list.subclasses.popular.TwitterPlaceViewModel;

import java.util.ArrayList;

public class NearestPlacesFragment extends BasePlaces {

    //Cambiar llamadas a viewmodel segun el tipo de lugares que se busque
    public NearestPlacesFragment(){
        super();
        placeList = new ArrayList<>();
    }
    @Override
    public void listPlaces() {
        super.mViewModel.listPlaces(page, quantum);
    }

    @Override
    public BaseViewModel getViewModelToParent() {
        NearestPlaceViewModel nvm = new ViewModelProvider(this).get(NearestPlaceViewModel.class);
        return nvm;
    }
}
