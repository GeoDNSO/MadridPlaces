package com.example.App.ui.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeTabAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;

    private List<Fragment> fragmentList;
    private List<String> titleList;

    public HomeTabAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        this.fragmentList = new ArrayList<>();
        this.titleList = new ArrayList<>();
    }

    public void addFragment(Fragment fragment, String title){
        this.fragmentList.add(fragment);
        this.titleList.add(title);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}
