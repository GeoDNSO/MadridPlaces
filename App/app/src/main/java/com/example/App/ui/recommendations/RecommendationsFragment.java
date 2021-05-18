package com.example.App.ui.recommendations;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.App.R;
import com.example.App.components.BaseTabAdapter;
import com.example.App.ui.recommendations.subclasses.RecommendationsFragmentFactory;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

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

    private void prepareViewPager() {
        BaseTabAdapter recoTabAdapter = new BaseTabAdapter(getChildFragmentManager());

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