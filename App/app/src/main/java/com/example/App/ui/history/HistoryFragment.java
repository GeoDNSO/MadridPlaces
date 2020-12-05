package com.example.App.ui.history;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.App.R;
import com.example.App.ui.aboutus.AboutUsViewModel;

public class HistoryFragment extends Fragment {

    private HistoryViewModel mViewModel;
    private View root;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        initializateVariables();
        root = inflater.inflate(R.layout.history_fragment, container, false);

        //TODO :Use the ViewModel to obtain Data/Implement observers

        return root;
    }

    private void initializateVariables(){
        mViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        // TODO: Use the ViewModel
    }

}