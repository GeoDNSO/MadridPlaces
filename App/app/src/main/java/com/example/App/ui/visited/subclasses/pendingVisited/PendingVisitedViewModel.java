package com.example.App.ui.visited.subclasses.pendingVisited;

import androidx.lifecycle.MutableLiveData;

import com.example.App.models.TPlace;
import com.example.App.ui.places_list.BaseViewModel;

import java.util.List;

public class PendingVisitedViewModel extends BaseViewModel {
    @Override
    protected MutableLiveData<List<TPlace>> getPlaceListToParent() {
        return placeRepository.getPendingToVisitList();
    }

    @Override
    public void listPlaceToParent(int page, int quant, String nickname, String searchText) {
        placeRepository.listPendingToVisit(page, quant, nickname, searchText);
    }
    // TODO: Implement the ViewModel
}