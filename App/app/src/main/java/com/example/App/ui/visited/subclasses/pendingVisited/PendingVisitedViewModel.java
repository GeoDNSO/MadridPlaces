package com.example.App.ui.visited.subclasses.pendingVisited;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.App.models.transfer.TPlace;
import com.example.App.ui.places_list.subclasses.BaseViewModel;

import java.util.List;

public class PendingVisitedViewModel extends BaseViewModel {
    @Override
    protected LiveData<List<TPlace>> getPlaceListToParent() {
        return null;
    }

    @Override
    public void listPlaceToParent(int page, int quant, String nickname, String serchText) {

    }
    // TODO: Implement the ViewModel
}