package com.example.App.ui.places_list.subclasses.nearest;

import androidx.lifecycle.LiveData;

import com.example.App.models.transfer.TPlace;
import com.example.App.ui.places_list.subclasses.BaseViewModel;

import java.util.List;

public class NearestPlaceViewModel extends BaseViewModel {

    //@TODO cambiar las llamadas al repositorio cuando esten la llamadas correspodientes a este view model

    @Override
    protected LiveData<List<TPlace>> getPlaceListToParent() {
        return placeRepository.getPlacesList();
    }

    @Override
    public void listPlaceToParent(int page, int quant) {
        placeRepository.listPlaces(page, quant);
    }

}
