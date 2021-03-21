package com.example.App.ui.places_list.subclasses.rating;

import androidx.lifecycle.LiveData;

import com.example.App.models.transfer.TPlace;
import com.example.App.ui.places_list.subclasses.BaseViewModel;

import java.util.List;

public class RatingPlaceViewModel extends BaseViewModel {
    @Override
    protected LiveData<List<TPlace>> getPlaceListToParent() {
        return placeRepository.getPlacesList();
    }

    @Override
    public void listPlaceToParent(int page, int quant) {
        placeRepository.listPlaces(page, quant);
    }

    @Override
    public void appendPlaceToParent(int page, int quant) {
        placeRepository.appendPlaces(page, quant);
    }
}
