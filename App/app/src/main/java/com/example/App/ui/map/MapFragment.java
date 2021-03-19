package com.example.App.ui.map;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.App.R;
import com.example.App.models.transfer.TPlace;
import com.example.App.utilities.AppConstants;

public class MapFragment extends Fragment {

    private MapViewModel mViewModel;

    private View root;

    private TPlace place;
    private TextView tvMapTest;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.map_fragment, container, false);

        tvMapTest = root.findViewById(R.id.tvMapTest);

        place = (TPlace) getArguments().getParcelable(AppConstants.BUNDLE_PLACE_DETAILS);

        tvMapTest.setText("Lugar --> Latitud: "+ place.getLatitude() + " Longitud: " + place.getLongitude());

        //Poner el nombre del lugar en la toolbar
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        actionBar.setTitle(place.getName());

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        // TODO: Use the ViewModel
    }

}