package com.example.App.ui.places_list.subclasses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class NearestPlacesFragment extends BasePlaces{

    //Cambiar llamadas a viewmodel segun el tipo de lugares que se busque
    public NearestPlacesFragment(){
        super();
        placeList = new ArrayList<>();
    }

    @Override
    public void appendPlaces() {
        super.mViewModel.appendPlaces(page, quantum);
    }

    @Override
    public void listPlaces() {
        super.mViewModel.listPlaces(page, quantum);
    }
}
