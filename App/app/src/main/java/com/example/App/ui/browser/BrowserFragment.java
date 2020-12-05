package com.example.App.ui.browser;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.App.R;

public class BrowserFragment extends Fragment {

    private BrowserViewModel mViewModel;

    public static BrowserFragment newInstance() {
        return new BrowserFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(BrowserViewModel.class);
        View root = inflater.inflate(R.layout.browser_fragment, container, false);

        //TODO :Use the ViewModel to obtain Data/Implement observers

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BrowserViewModel.class);
        // TODO: Use the ViewModel
    }

}