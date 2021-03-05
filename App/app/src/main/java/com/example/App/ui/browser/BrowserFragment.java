package com.example.App.ui.browser;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.App.R;
import com.example.App.ui.admin.UserListAdapter;
import com.example.App.ui.places_list.PlacesListFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class BrowserFragment extends Fragment {

    private BrowserViewModel mViewModel;
    private Fragment placeListFragment;
    private View root;
    private List<String> listTypesPlaces;
    private ChipGroup chipGroupView;
    private Button buttonBrowserView;
    private List<String> listTypePlaces;


    public static BrowserFragment newInstance() {
        return new BrowserFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(BrowserViewModel.class);
        root = inflater.inflate(R.layout.browser_fragment, container, false);
        setHasOptionsMenu(true);

        initUI();
        initializeListeners();

        listTypesPlaces = mViewModel.getTypesOfPlaces();

        for(String typePlace : listTypesPlaces) {
            listTypePlaces = new ArrayList<>();
            Chip chip = (Chip) LayoutInflater.from(getContext()).inflate(R.layout.type_place_list_fragment,null);
            chip.setText(typePlace);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(chip.isChecked()){
                        listTypePlaces.add(chip.getText().toString());
                    }
                    else {
                        listTypePlaces.remove(chip.getText().toString());
                    }
                }
            });
            chipGroupView.addView(chip);
        }
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BrowserViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_icon_menu, menu);

        //Ocultar el boton de AZ
        MenuItem azIcon = menu.findItem(R.id.sortListUsers);
        azIcon.setVisible(false);

        //Boton de busqueda
        MenuItem searchIcon = menu.findItem(R.id.search_button);
        SearchView searchView = (SearchView) searchIcon.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void initializeListeners() {
        buttonBrowserView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), listTypePlaces.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI(){
        chipGroupView = root.findViewById(R.id.type_of_place_list);
        buttonBrowserView = root.findViewById(R.id.browser_button);
    }
}