package com.example.App.ui.places_list.subclasses.popular;

import androidx.lifecycle.MutableLiveData;

import com.example.App.models.TPlace;
import com.example.App.ui.places_list.BaseViewModel;

import java.util.List;

public class TwitterPlaceViewModel extends BaseViewModel {

    //@TODO cambiar las llamadas al repositorio cuando esten la llamadas correspodientes a este view model

    @Override
    protected MutableLiveData<List<TPlace>> getPlaceListToParent() {
        return placeRepository.getTwitterPlacesList();
    }

    @Override
    public void listPlaceToParent(int page, int quant, String nickname, String searchText) {
        placeRepository.listTwitterPlaces(page, quant, nickname, searchText);
    }
}
