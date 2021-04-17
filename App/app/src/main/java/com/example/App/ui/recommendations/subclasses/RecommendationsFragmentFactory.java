package com.example.App.ui.recommendations.subclasses;

import androidx.fragment.app.Fragment;

import com.example.App.App;
import com.example.App.R;

import com.example.App.ui.recommendations.subclasses.my_recommendations.MyRecommendationsFragment;
import com.example.App.ui.recommendations.subclasses.pending_recommendations.PendingRecommendationsFragment;


public class RecommendationsFragmentFactory {

    public Fragment getInstance(String type, String category){
        App app = App.getInstance();

        String myReco = app.getAppString(R.string.my_recommedations);
        String pedingReco = app.getAppString(R.string.pending_recomendations);

        if(type.equals(myReco)){
            return new MyRecommendationsFragment();
        }
        if(type.equals(pedingReco)){
            return new PendingRecommendationsFragment();
        }
        return null;
    }
}
