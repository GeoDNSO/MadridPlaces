package com.example.App.ui.visited.subclasses.pendingVisited;

import androidx.lifecycle.LiveData;

import com.example.App.models.TPlace;
import com.example.App.ui.places_list.subclasses.BaseViewModel;

import java.util.List;

public class PendingVisitedViewModel extends BaseViewModel {
    @Override
    protected LiveData<List<TPlace>> getPlaceListToParent() {
        return placeRepository.getPendingToVisitList();
    }

    @Override
    public void listPlaceToParent(int page, int quant, String nickname, String serchText) {
        placeRepository.listPendingToVisit(page, quant, nickname, serchText);
    }
    // TODO: Implement the ViewModel
}