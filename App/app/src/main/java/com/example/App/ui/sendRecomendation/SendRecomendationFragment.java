package com.example.App.ui.sendRecomendation;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.App.R;

public class SendRecomendationFragment extends Fragment {

    private View root;
    private SendRecomendationViewModel mViewModel;

    //Elementos visuales
    private TextView tv_usuarioDestino;
    private Button sendRecomendationButton;


    public static SendRecomendationFragment newInstance() {
        return new SendRecomendationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.send_recomendation_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SendRecomendationViewModel.class);
        // TODO: Use the ViewModel
    }

}