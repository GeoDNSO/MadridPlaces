package com.example.App.ui.places_list.subclasses.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.App.App;
import com.example.App.models.TCategory;
import com.example.App.services.LocationTrack;
import com.example.App.ui.places_list.BasePlaces;
import com.example.App.ui.places_list.BaseViewModel;
import com.example.App.utilities.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class CategoryPlacesFragment extends BasePlaces {

    //Cambiar llamadas a viewmodel segun el tipo de lugares que se busque
    protected String category;

    public static CategoryPlacesFragment newInstance(String category) {
        Bundle args = new Bundle();
        args.putString("category", category);
        CategoryPlacesFragment f = new CategoryPlacesFragment();
        f.setArguments(args);
        return f;
    }

    public CategoryPlacesFragment(){
        super();
        placeList = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        TCategory tCategory = (TCategory) getArguments().getParcelable(AppConstants.BUNDLE_CATEGORY_TYPE);
        category = tCategory.getName();

        //Poner el nombre de la categoria en la toolbar
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        actionBar.setTitle(category);

        //Primero es necesario instanciar el tipo de categoria de este fragmento para que la llamada a super
        //no ejecute con valores nulos la llamada a la BD
        View superView =  super.onCreateView(inflater, container, savedInstanceState);

        return superView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        addPlace.setVisible(false);

        nearestPlaces.setVisible(true);
        nearestPlaces.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                LocationTrack locationTrack = App.getInstance().getLocationTrack();
                List<Double> points = new ArrayList<>();
                points.add(locationTrack.getLongitude());
                points.add(locationTrack.getLatitude());

                ((CategoryPlaceViewModel)mViewModel).listNearestCategories(page, quantum, App.getInstance().getUsername(), search_text, points);
                return true;
            }
        });
    }

    @Override
    public void listPlaces() { super.mViewModel.listPlaces(page, quantum, App.getInstance().getUsername(), search_text);  }

    @Override
    public BaseViewModel getViewModelToParent() {
        CategoryPlaceViewModel cvm = new ViewModelProvider(this).get(CategoryPlaceViewModel.class);
        cvm.setCategory(category);
        return cvm;
    }

}
