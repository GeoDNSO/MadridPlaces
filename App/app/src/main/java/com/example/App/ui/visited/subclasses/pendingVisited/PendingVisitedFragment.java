package com.example.App.ui.visited.subclasses.pendingVisited;

import androidx.lifecycle.ViewModelProvider;

import com.example.App.App;
import com.example.App.ui.places_list.BasePlaces;
import com.example.App.ui.places_list.BaseViewModel;

import java.util.ArrayList;

public class PendingVisitedFragment extends BasePlaces {

    public PendingVisitedFragment(){
        super();
        placeList = new ArrayList<>();
    }
    @Override
    public void listPlaces() {
        super.mViewModel.listPlaces(page, quantum, App.getInstance().getUsername(), search_text);
    }

    @Override
    public BaseViewModel getViewModelToParent() {
        PendingVisitedViewModel rvm = new ViewModelProvider(this).get(PendingVisitedViewModel.class);
        return rvm;
    }

}