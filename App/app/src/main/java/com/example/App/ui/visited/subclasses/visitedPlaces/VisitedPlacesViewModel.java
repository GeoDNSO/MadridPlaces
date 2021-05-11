package com.example.App.ui.visited.subclasses.visitedPlaces;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.App.models.TPlace;
import com.example.App.ui.places_list.subclasses.BaseViewModel;

import java.util.List;

public class VisitedPlacesViewModel extends BaseViewModel {


    @Override
    protected MutableLiveData<List<TPlace>> getPlaceListToParent() {
        return placeRepository.getPlaceVisitedList();
    }

    @Override
    public void listPlaceToParent(int page, int quant, String nickname, String serchText) {
        placeRepository.listVisitedPlaces(page, quant, nickname, serchText);
    }
    // TODO: Implement the ViewModel
}