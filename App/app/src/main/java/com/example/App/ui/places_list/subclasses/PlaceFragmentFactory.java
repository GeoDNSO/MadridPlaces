package com.example.App.ui.places_list.subclasses;

import com.example.App.utilities.AppConstants;

public class PlaceFragmentFactory {

    public BasePlaces getInstance(String type, String category){
        if(type.equals(AppConstants.TAB_RATING)){
            return new RatingPlacesFragment();
        }
        if(type.equals(AppConstants.TAB_NEAREST)){
            return new NearestPlacesFragment();
        }
        if(type.equals(AppConstants.TAB_TWITTER)){
            return new TwitterPlacesFragment();
        }
        if(type.equals(AppConstants.TAB_CATEGORY)){
            return CategoryPlacesFragment.newInstance(category);
        }
        return null;
    }
}
