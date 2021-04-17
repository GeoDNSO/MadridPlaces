package com.example.App.ui.visited;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.App.models.repositories.PlaceRepository;
import com.example.App.models.transfer.TPlace;
import com.example.App.ui.ViewModelParent;

import java.util.List;

public class VisitedViewModel extends ViewModelParent {

    private MutableLiveData<Boolean> mProgressBar = new MutableLiveData<>(); //true indica progress bar activo

    public void init() {

    }


}