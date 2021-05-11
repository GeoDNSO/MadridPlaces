package com.example.App.ui.places_list.subclasses.nearest;

import androidx.lifecycle.LiveData;

import com.example.App.models.TPlace;
import com.example.App.ui.places_list.subclasses.BaseViewModel;

import java.util.List;

public class NearestPlaceViewModel extends BaseViewModel {

    private List<Double> points;

    @Override
    protected LiveData<List<TPlace>> getPlaceListToParent() {
        return placeRepository.getNearestPlacesList();
    }

    @Override
    public void listPlaceToParent(int page, int quant, String nickname, String searchText) {

        /*
        //Asignar los puntos antes de realizar la busqueda
        LocationTrack locationTrack = App.getInstance().getLocationTrack();

        List<Double> points = new ArrayList<>();
        points.add(locationTrack.getLongitude());
        points.add(locationTrack.getLatitude());

        Log.d("NEAREST_LIST", "LAT: "+ locationTrack.getLatitude() +  " LONG" + locationTrack.getLongitude());

        setPoints(points);

        */

        placeRepository.listNearestPlaces(page, quant, nickname, this.points, searchText);
    }

    public void setPoints(List<Double> points) {
        this.points = points;
    }

}
