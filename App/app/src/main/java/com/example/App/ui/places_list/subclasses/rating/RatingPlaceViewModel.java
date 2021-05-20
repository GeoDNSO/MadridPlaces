package com.example.App.ui.places_list.subclasses.rating;

import androidx.lifecycle.MutableLiveData;

import com.example.App.models.TPlace;
import com.example.App.ui.places_list.BaseViewModel;

import java.util.List;

public class RatingPlaceViewModel extends BaseViewModel {
    @Override
    protected MutableLiveData<List<TPlace>> getPlaceListToParent() {
        return placeRepository.getPlacesList();
    }

    @Override
    public void listPlaceToParent(int page, int quant, String nickname, String searchText) {
        placeRepository.listPlaces(page, quant, nickname, searchText);
    }
}
