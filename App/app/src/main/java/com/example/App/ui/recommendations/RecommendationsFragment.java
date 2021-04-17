package com.example.App.ui.recommendations;

import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.App.R;
import com.example.App.models.dao.SimpleRequest;
import com.example.App.models.transfer.TPlace;
import com.example.App.ui.categories.CategoriesFragment;
import com.example.App.ui.home.HomeTabAdapter;
import com.example.App.ui.places_list.subclasses.PlaceFragmentFactory;
import com.example.App.ui.recommendations.subclasses.RecommendationsFragmentFactory;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecommendationsFragment extends Fragment {

    private RecommendationsViewModel mViewModel;
    private View root;


    private List<String> tabTitlesList;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    protected int page = 1, limit = 3, quantum = 3;

    public static RecommendationsFragment newInstance() {
        return new RecommendationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(RecommendationsViewModel.class);
        root = inflater.inflate(R.layout.recommendations_fragment, container, false);
        setHasOptionsMenu(true);

        initUI();
        initializeListeners();

        tabTitlesList = new ArrayList<>();
        tabTitlesList.add(getString(R.string.my_recommedations));
        tabTitlesList.add(getString(R.string.pending_recomendations));

        tabLayout.setupWithViewPager(viewPager);

        prepareViewPager();


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RecommendationsViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.recommendations_menu, menu);

        MenuItem sendRecommendation = menu.findItem(R.id.add_recommendation_menu_item);


        sendRecommendation.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                return false;
            }
        });
    }

    private void prepareViewPager() {
        RecommendationsTabAdapter recoTabAdapter = new RecommendationsTabAdapter(getChildFragmentManager());

        RecommendationsFragmentFactory recoFragmentFactory = new RecommendationsFragmentFactory();

        for(int i = 0; i < tabTitlesList.size(); i++){
            Fragment placeListFragment = recoFragmentFactory.getInstance(tabTitlesList.get(i), null);
            recoTabAdapter.addFragment(placeListFragment, tabTitlesList.get(i));
        }

        viewPager.setAdapter(recoTabAdapter);
    }

    private void initializeListeners() {


    }

    private void initUI(){
        tabLayout = root.findViewById(R.id.recommendations_tab_layout);
        viewPager = root.findViewById(R.id.recommendations_view_pager);
    }
}