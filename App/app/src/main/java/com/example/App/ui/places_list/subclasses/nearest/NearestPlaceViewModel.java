package com.example.App.ui.places_list.subclasses.nearest;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.App.models.transfer.TPlace;
import com.example.App.ui.places_list.subclasses.BaseViewModel;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.util.List;

public class NearestPlaceViewModel extends BaseViewModel {

    private List<Double> points;

    @Override
    protected LiveData<List<TPlace>> getPlaceListToParent() {
        return placeRepository.getNearestPlacesList();
    }

    @Override
    public void listPlaceToParent(int page, int quant, String nickname) {
        placeRepository.listNearestPlaces(page, quant, nickname, this.points);
    }

    public void setPoints(List<Double> points) {
        this.points = points;
    }

}
