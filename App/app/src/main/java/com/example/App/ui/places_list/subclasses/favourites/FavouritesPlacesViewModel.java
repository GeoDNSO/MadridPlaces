package com.example.App.ui.places_list.subclasses.favourites;

import androidx.lifecycle.MutableLiveData;

import com.example.App.models.TPlace;
import com.example.App.ui.places_list.BaseViewModel;

import java.util.List;

public class FavouritesPlacesViewModel extends BaseViewModel {
    @Override
    protected MutableLiveData<List<TPlace>> getPlaceListToParent() {
        return placeRepository.getFavouritesPlacesList();
    }

    @Override
    public void listPlaceToParent(int page, int quant, String nickname, String searchText) {
        placeRepository.listFavouritesPlaces(page, quant, nickname, searchText);
    }
}
