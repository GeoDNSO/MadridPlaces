package com.example.App.ui.visited.subclasses.visitedPlaces;

import androidx.lifecycle.MutableLiveData;

import com.example.App.models.TPlace;
import com.example.App.ui.places_list.BaseViewModel;

import java.util.List;

public class VisitedPlacesViewModel extends BaseViewModel {


    @Override
    protected MutableLiveData<List<TPlace>> getPlaceListToParent() {
        return placeRepository.getPlaceVisitedList();
    }

    @Override
    public void listPlaceToParent(int page, int quant, String nickname, String searchText) {
        placeRepository.listVisitedPlaces(page, quant, nickname, searchText);
    }
    // TODO: Implement the ViewModel
}