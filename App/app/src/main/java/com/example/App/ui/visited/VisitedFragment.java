package com.example.App.ui.visited;

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
import com.example.App.ui.visited.subclasses.VisitedFragmentFactory;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class VisitedFragment extends Fragment{

    private VisitedViewModel mViewModel;
    private View root;

    private List<String> tabTitlesList;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    public static VisitedFragment newInstance() {
        return new VisitedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(VisitedViewModel.class);
        root = inflater.inflate(R.layout.visited_fragment, container, false);

        mViewModel.init();

        initUI();
        initializeListeners();

        tabTitlesList = new ArrayList<>();
        tabTitlesList.add(getString(R.string.profile_visited_places));
        tabTitlesList.add(getString(R.string.pending_visited_text));

        tabLayout.setupWithViewPager(viewPager);

        prepareViewPager();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(VisitedViewModel.class);
        // TODO: Use the ViewModel
    }

    private void prepareViewPager() {
        BaseTabAdapter visitedTabAdapter = new BaseTabAdapter(getChildFragmentManager());

        VisitedFragmentFactory visitedFragmentFactory = new VisitedFragmentFactory();

        for(int i = 0; i < tabTitlesList.size(); i++){
            Fragment placeListFragment = visitedFragmentFactory.getInstance(tabTitlesList.get(i), null);
            visitedTabAdapter.addFragment(placeListFragment, tabTitlesList.get(i));
        }

        viewPager.setAdapter(visitedTabAdapter);
    }

    private void initializeListeners() {


    }

    private void initUI(){
        tabLayout = root.findViewById(R.id.recommendations_tab_layout);
        viewPager = root.findViewById(R.id.recommendations_view_pager);
    }

}