package com.example.App.ui.categories;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.App.R;
import com.example.App.models.transfer.TCategory;
import com.example.App.ui.add_place.AddPlaceViewModel;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.ViewListenerUtilities;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment implements CategoriesAdapter.CategoryListener {


    private List<String> categoriesTitles;
    private List<Integer> categoriesIcons;
    private List<TCategory> categoryList;
    private CategoriesAdapter categoriesAdapter;


    private View root;

    private CategoriesViewModel mViewModel;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.categories_fragment, container, false);


        mViewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);
        mViewModel.init();
        initCategories();

        progressBar = root.findViewById(R.id.category_progressBar);
        recyclerView = root.findViewById(R.id.category_RecyclerView);

        categoryList = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapter(getActivity(), categoryList, this);

        //Set layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(categoriesAdapter);

        initObservers();

        mViewModel.getTypesOfPlaces();

        return root;
    }

    private void initObservers() {
        mViewModel.getProgressBar().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                ViewListenerUtilities.setVisibility(progressBar, aBoolean);
            }
        });

        mViewModel.getmCategoriesSuccess().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> categoriesList) {
                categoriesTitles = categoriesList;
                adaptDataToCategories();
                categoriesAdapter = new CategoriesAdapter(getActivity(), categoryList, CategoriesFragment.this);
                recyclerView.setAdapter(categoriesAdapter);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);
    }

    public void adaptDataToCategories(){
        categoryList = new ArrayList<>();
        for(int i = 0; i < categoriesTitles.size(); ++i){
            categoryList.add(new TCategory(categoriesTitles.get(i), categoriesIcons.get(i)));
        }
    }

    public void initCategories(){
        categoriesIcons = new ArrayList<>();
        categoriesIcons.add(R.drawable.ic_hotel);
        categoriesIcons.add(R.drawable.ic_club);
        categoriesIcons.add(R.drawable.ic_monument);
        categoriesIcons.add(R.drawable.ic_exhibition_museum);
        categoriesIcons.add(R.drawable.ic_direccion_turismo);
        categoriesIcons.add(R.drawable.ic_fork_restaurant);
        categoriesIcons.add(R.drawable.ic_church);
        categoriesIcons.add(R.drawable.ic_shop);

    }

    @Override
    public void onClicListener(int position) {
        //Enviar datos del objeto con posicion position de la lista al otro fragment
        Toast.makeText(getActivity(), "Touch Listener Category", Toast.LENGTH_SHORT);
        TCategory category = categoryList.get(position);

        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.BUNDLE_CATEGORY_TYPE, category);

        //Le pasamos el bundle
        Navigation.findNavController(root).navigate(R.id.categoryPlacesFragment, bundle);
    }
}