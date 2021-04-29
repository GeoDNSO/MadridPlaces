package com.example.App.ui.friends;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.App.R;
import com.example.App.ui.friends.subclasses.FriendsFragmentFactory;
import com.example.App.ui.recommendations.RecommendationsTabAdapter;
import com.example.App.ui.recommendations.RecommendationsViewModel;
import com.example.App.ui.visited.subclasses.VisitedFragmentFactory;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    private FriendsViewModel mViewModel;
    private View root;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private List<String> tabTitlesList;

    public static FriendsFragment newInstance() {
        return new FriendsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.friends_fragment, container, false);
        mViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        setHasOptionsMenu(true);

        initUI();
        initializeListeners();

        tabTitlesList = new ArrayList<>();
        tabTitlesList.add(getString(R.string.friend_list));
        tabTitlesList.add(getString(R.string.friend_request));

        tabLayout.setupWithViewPager(viewPager);

        prepareViewPager();

        return root;
    }

    private void initializeListeners() {

    }

    private void initUI() {
        tabLayout = root.findViewById(R.id.friends_tab_layout);
        viewPager = root.findViewById(R.id.friends_view_pager);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        // TODO: Use the ViewModel
    }

    private void prepareViewPager() {
        RecommendationsTabAdapter visitedTabAdapter = new RecommendationsTabAdapter(getChildFragmentManager());

        FriendsFragmentFactory friendsFragmentFactory = new FriendsFragmentFactory();

        for(int i = 0; i < tabTitlesList.size(); i++){
            Fragment placeListFragment = friendsFragmentFactory.getInstance(tabTitlesList.get(i));
            visitedTabAdapter.addFragment(placeListFragment, tabTitlesList.get(i));
        }

        viewPager.setAdapter(visitedTabAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.friends_menu, menu);

        MenuItem addFriendMenuItem = menu.findItem(R.id.add_friend_menu_item);

        addFriendMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //TODO --> mandar a fragment de añadir amigo...
                return false;
            }
        });
    }


}