package com.example.App.ui.sendRecomendation;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.App.App;
import com.example.App.R;
import com.example.App.models.transfer.TPlace;
import com.example.App.models.transfer.TRecomendation;
import com.example.App.utilities.AppConstants;
import com.example.App.utilities.Validator;

public class SendRecomendationFragment extends Fragment {

    private View root;
    private SendRecomendationViewModel mViewModel;
    private TPlace place;

    //Elementos visuales
    private EditText et_usuarioDestino;
    private Button sendRecomendationButton;

    private TRecomendation recomendation;
    private App app;

    public static SendRecomendationFragment newInstance() {
        return new SendRecomendationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.send_recomendation_fragment, container, false);

        // setHasOptionsMenu(true);

        mViewModel = new ViewModelProvider(this).get(SendRecomendationViewModel.class);
        mViewModel.init();

        viewModelListeners();

        initializeUI();

        place = (TPlace) getArguments().getParcelable(AppConstants.BUNDLE_PLACE_DETAILS);

        initializeListeners();

        return root;
    }

    private void viewModelListeners() {
        mViewModel.getSendingSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer.equals(AppConstants.SEND_REC_OK)){
                    Toast.makeText(getActivity(), "ENVIADA!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SendRecomendationViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initializeListeners() {
        sendRecomendationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRecomendationAction(v);
            }
        });

    }

    private void initializeUI() {
        et_usuarioDestino = (EditText) root.findViewById(R.id.send_reco);
        sendRecomendationButton = (Button) root.findViewById(R.id.addFriendButton);
    }

    private void sendRecomendationAction(View v){
        app = App.getInstance(getActivity());
        String userDest = et_usuarioDestino.getText().toString();
        String userOrigin = app.getUsername();
        String placeName = "tu pasta";
        if (Validator.argumentsEmpty(userDest)) {
            Toast.makeText(getActivity(), getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
        }
        else {
            placeName = place.getName();
            Toast.makeText(getActivity(), placeName, Toast.LENGTH_SHORT).show();
            mViewModel.sendRecomendation(userOrigin, userDest, placeName);
            //Toast.makeText(getActivity(), "Recom enviada", Toast.LENGTH_SHORT).show();
        }
    }
}