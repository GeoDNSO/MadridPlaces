package com.example.App.ui.home;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import com.example.App.App;
import com.example.App.R;

import com.example.App.components.BaseTabAdapter;
import com.example.App.ui.categories.CategoriesFragment;

import com.example.App.ui.places_list.subclasses.PlaceFragmentFactory;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private View root;
    private HomeViewModel mViewModel;

    private App app; //global variable

    private List<String> tabTitlesList;
    private TabLayout tabLayout;
    private ViewPager viewPager;



    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.home_fragment, container, false);
        //setHasOptionsMenu(true);
        app = App.getInstance();

        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        //Acciones de inicialización del fragment
        initializeUI();
        initializeListeners();
        viewAccToUser();
        actionOnServerAvailable();


        //TabLayout
        tabTitlesList = new ArrayList<>();
        tabTitlesList.add(getString(R.string.tab_rating));
        tabTitlesList.add(getString(R.string.tab_nearest));
        tabTitlesList.add(getString(R.string.tab_twitter));
        tabTitlesList.add(getString(R.string.tab_category));

        tabLayout.setupWithViewPager(viewPager);

        prepareViewPager();

        /*
        placeListFragment = new PlacesListFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.place_list_container, placeListFragment).commit();
        */
      
        return root;
    }

    private void prepareViewPager() {
        BaseTabAdapter homeTabAdapter = new BaseTabAdapter(getChildFragmentManager());

        PlaceFragmentFactory placeFragmentFactory = new PlaceFragmentFactory();

        for(int i = 0; i < tabTitlesList.size()-1; i++){
            Fragment placeListFragment = placeFragmentFactory.getInstance(tabTitlesList.get(i), null);
            homeTabAdapter.addFragment(placeListFragment, tabTitlesList.get(i));
        }
        Fragment categoryFragment = new CategoriesFragment();
        int last = (tabTitlesList.size()-1);
        homeTabAdapter.addFragment(categoryFragment, tabTitlesList.get(last));

        viewPager.setAdapter(homeTabAdapter);
    }

    //View according to the user's status (logged or not)
    private void viewAccToUser() {

        if (app.isLogged()) {
            app.menuOptions(app.isLogged(), app.isAdmin());
        }
        else {
            //Nothing...
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initializeListeners() {
       //Empty...
    }

    private void initializeUI() {
        tabLayout = root.findViewById(R.id.home_tab_layout);
        viewPager = root.findViewById(R.id.home_view_pager);

    }

    private void actionOnServerAvailable(){

       if(App.isServerReachable()){

       }
       else{

       }
    }

}