package com.example.App.ui.places_list.subclasses.popular;

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

public class TwitterPlacesFragment extends BasePlaces{

    //Cambiar llamadas a viewmodel segun el tipo de lugares que se busque
    public TwitterPlacesFragment(){
        super();
        placeList = new ArrayList<>();
    }
    @Override
    public void listPlaces() {
        super.mViewModel.listPlaces(page, quantum, App.getInstance(getContext()).getUsername(), search_text);
    }

    @Override
    public BaseViewModel getViewModelToParent() {
        TwitterPlaceViewModel tvm = new ViewModelProvider(this).get(TwitterPlaceViewModel.class);
        return tvm;
    }
}
