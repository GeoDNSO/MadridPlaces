package com.example.App.ui.visited.subclasses;

import androidx.fragment.app.Fragment;

import com.example.App.App;
import com.example.App.R;
import com.example.App.ui.visited.VisitedFragment;
import com.example.App.ui.visited.subclasses.pendingVisited.PendingVisitedFragment;
import com.example.App.ui.visited.subclasses.visitedPlaces.VisitedPlacesFragment;

public class VisitedFragmentFactory {

    public Fragment getInstance(String type, String category){
        App app = App.getInstance();

        String visitedString = app.getAppString(R.string.profile_visited_places);
        String pendingVisited = app.getAppString(R.string.pending_visited_text);

        if(type.equals(visitedString)){
            return new VisitedPlacesFragment();
        }
        if(type.equals(pendingVisited)){
            return new PendingVisitedFragment();
        }
        return null;
    }
}
