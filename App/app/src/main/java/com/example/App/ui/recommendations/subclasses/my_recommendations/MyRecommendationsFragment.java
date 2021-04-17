package com.example.App.ui.recommendations.subclasses.my_recommendations;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.App.R;

public class MyRecommendationsFragment extends Fragment {

    private MyRecommendationsViewModel mViewModel;

    public static MyRecommendationsFragment newInstance() {
        return new MyRecommendationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_recommendations_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MyRecommendationsViewModel.class);
        // TODO: Use the ViewModel
    }

}