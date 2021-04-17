package com.example.App.ui.places_list.subclasses.favourites;

import androidx.lifecycle.LiveData;

import com.example.App.models.transfer.TPlace;
import com.example.App.ui.places_list.subclasses.BaseViewModel;

import java.util.List;

public class FavouritesPlacesViewModel extends BaseViewModel {
    @Override
    protected LiveData<List<TPlace>> getPlaceListToParent() {
        return placeRepository.getFavouritesPlacesList();
    }

    @Override
    public void listPlaceToParent(int page, int quant, String nickname, String serchText) {
        placeRepository.listFavouritesPlaces(page, quant, nickname, serchText);
    }
}
