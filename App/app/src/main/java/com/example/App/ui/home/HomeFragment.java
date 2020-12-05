package com.example.App.ui.home;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.App.App;
import com.example.App.R;
import com.example.App.SessionManager;

public class HomeFragment extends Fragment {

    private View root;
    private HomeViewModel mViewModel;

    private Button btn_register;
    private Button btn_login;
    private Button btn_logout;
    private App app; //global variable

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.home_fragment, container, false);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        initializeUI();

        //crear una app
        app = new App();

        //TODO :Use the ViewModel to obtain Data/Implement observers

        if(!app.getUsername().isEmpty()) {
            btn_register.setVisibility(View.GONE);
            btn_login.setVisibility(View.GONE);
            btn_logout.setVisibility(View.VISIBLE);
        }else{
            btn_register.setVisibility(View.VISIBLE);
            btn_login.setVisibility(View.VISIBLE);
            btn_logout.setVisibility(View.GONE);
        }

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_registerFragment);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_loginFragment);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.logout();
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_loginFragment);
            }
        });

        return root;
    }

    private void initializeUI(){
        btn_register = root.findViewById(R.id.home_register_button);
        btn_login = root.findViewById(R.id.home_login_button);
        btn_logout = root.findViewById(R.id.home_logout_button);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }

}