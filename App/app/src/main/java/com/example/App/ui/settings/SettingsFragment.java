package com.example.App.ui.settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.App.R;
import com.example.App.utilities.PermissionsManager;

public class SettingsFragment extends Fragment {

    private SettingsViewModel mViewModel;
    private View root;

    private Switch geoSwitch;
    private PermissionsManager permissionsManager;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        root = inflater.inflate(R.layout.settings_fragment, container, false);

        //TODO :Use the ViewModel to obtain Data/Implement observers

        geoSwitch = root.findViewById(R.id.settings_switch_geo);
        permissionsManager = new PermissionsManager(getActivity());

        geoSwitch.setChecked(permissionsManager.hasGeolocationPermissions());

        initListeners();

        return root;
    }

    private void initListeners() {
        geoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String[] permissions = new String[] { Manifest.permission.ACCESS_FINE_LOCATION};
                    permissionsManager.startPermissionsActivity(permissions, permissionsManager.GEOLOCATION_REQUEST_CODE);
                    geoSwitch.setChecked(permissionsManager.hasGeolocationPermissions());
                }
                else{
                    permissionsManager.userPermissionsIntent();
                    geoSwitch.setChecked(permissionsManager.hasGeolocationPermissions());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        geoSwitch.setChecked(permissionsManager.hasGeolocationPermissions());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        // TODO: Use the ViewModel
    }

}