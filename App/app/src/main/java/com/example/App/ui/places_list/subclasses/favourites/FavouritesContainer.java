package com.example.App.ui.places_list.subclasses.favourites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.App.App;
import com.example.App.R;
import com.example.App.ui.admin.AdminFragment;

public class FavouritesContainer extends Fragment {
    private View root;
    private FrameLayout frameLayout;
    private FavouritesPlacesFragment favouritesListFragment;

    public static FavouritesContainer newInstance() {
        return new FavouritesContainer();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.favourites_fragment, container, false);

        favouritesListFragment = new FavouritesPlacesFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.favourites_list_container, favouritesListFragment).commit();

        return root;
    }

}
