package com.example.App.ui.places_list.subclasses.rating;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.App.App;
import com.example.App.ui.places_list.subclasses.BasePlaces;
import com.example.App.ui.places_list.subclasses.BaseViewModel;
import com.example.App.ui.places_list.subclasses.category.CategoryPlaceViewModel;

import java.util.ArrayList;

public class RatingPlacesFragment extends BasePlaces {

    //Cambiar llamadas a viewmodel segun el tipo de lugares que se busque

    public RatingPlacesFragment(){
        super();
        placeList = new ArrayList<>();
    }
    @Override
    public void listPlaces() {
        super.mViewModel.listPlaces(page, quantum, App.getInstance(getContext()).getUsername());
    }

    @Override
    public BaseViewModel getViewModelToParent() {
        RatingPlaceViewModel rvm = new ViewModelProvider(this).get(RatingPlaceViewModel.class);
        return rvm;
    }
}
