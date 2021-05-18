package com.example.App.ui.places_list.subclasses.nearest;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import com.example.App.App;
import com.example.App.R;
import com.example.App.services.LocationTrack;
import com.example.App.ui.places_list.subclasses.BasePlaces;
import com.example.App.ui.places_list.subclasses.BaseViewModel;
import com.example.App.ui.places_list.subclasses.popular.TwitterPlaceViewModel;
import com.example.App.utilities.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class NearestPlacesFragment extends BasePlaces {

    public NearestPlacesFragment(){
        super();
        placeList = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View superRoot = super.onCreateView(inflater, container, savedInstanceState);

        super.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //Sobrescribimos el nestedscrollview porque en los lugares más cercanos no vamos
                //realizar nuevas peticiones al llegar al final, la lista es fija
            }
        });

        return superRoot;
    }

    @Override
    public void listPlaces() {

        //Asignar los puntos antes de realizar la busqueda
        LocationTrack locationTrack = App.getInstance().getLocationTrack();

        List<Double> points = new ArrayList<>();
        points.add(locationTrack.getLongitude());
        points.add(locationTrack.getLatitude());

        Log.d("NEAREST_LIST", "LAT: "+ locationTrack.getLatitude() +  " LONG" + locationTrack.getLongitude());

        ((NearestPlaceViewModel) mViewModel).setPoints(points);

        super.mViewModel.listPlaces(page, quantum, App.getInstance().getUsername(), search_text);
    }

    @Override
    public BaseViewModel getViewModelToParent() {
        NearestPlaceViewModel nvm = new ViewModelProvider(this).get(NearestPlaceViewModel.class);
        return nvm;
    }
}
