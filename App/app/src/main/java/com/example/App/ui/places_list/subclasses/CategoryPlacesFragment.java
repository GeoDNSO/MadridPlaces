package com.example.App.ui.places_list.subclasses;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.App.models.transfer.TCategory;
import com.example.App.utilities.AppConstants;

import java.util.ArrayList;

public class CategoryPlacesFragment extends BasePlaces{

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

        //Poner el nombre del lugar en la toolbar
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        actionBar.setTitle(category);

        //Primero es necesario instanciar el tipo de categoria de este fragmento para que la llamada a super
        //no ejecute con valores nulos la llamada a la BD
        View superView =  super.onCreateView(inflater, container, savedInstanceState);

        return superView;
    }

    @Override
    public void appendPlaces() {
        super.mViewModel.appendPlaces(page, quantum, this.category);
    }

    @Override
    public void listPlaces() {
        super.mViewModel.listPlaces(page, quantum, this.category);
    }
}
