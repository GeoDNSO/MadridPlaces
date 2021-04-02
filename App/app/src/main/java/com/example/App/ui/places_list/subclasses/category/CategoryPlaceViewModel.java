package com.example.App.ui.places_list.subclasses.category;

import androidx.lifecycle.LiveData;

import com.example.App.models.transfer.TPlace;
import com.example.App.ui.places_list.subclasses.BaseViewModel;

import java.util.List;

public class CategoryPlaceViewModel extends BaseViewModel {

    protected String category;

    @Override
    protected LiveData<List<TPlace>> getPlaceListToParent() {
        return placeRepository.getCategoriesPlacesList();
    }

    @Override
    public void listPlaceToParent(int page, int quant, String nickname) {
        placeRepository.listPlacesCategories(page, quant, nickname, category);
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
