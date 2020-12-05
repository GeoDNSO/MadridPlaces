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

import com.example.App.R;
import com.example.App.SessionManager;
import com.example.App.ui.aboutus.AboutUsViewModel;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    private View root;
    private Button btn_register;
    private Button btn_login;
    private Button btn_logout;
    private SessionManager session; //global variable

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        initializateVariables();
        root = inflater.inflate(R.layout.home_fragment, container, false);

        //TODO :Use the ViewModel to obtain Data/Implement observers

        //comprueba si el campo de usuario esta vacío.
        if(!session.getUsername().isEmpty()) {
            btn_register.setVisibility(View.GONE);
            btn_login.setVisibility(View.GONE);
            btn_logout.setVisibility(View.VISIBLE);
        }else{
            btn_register.setVisibility(View.VISIBLE);
            btn_login.setVisibility(View.VISIBLE);
            btn_logout.setVisibility(View.GONE);
        }
        //funciones de los botones
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
                session.logout();
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_loginFragment);
            }
        });

        return root;
    }

    private void initializateVariables(){
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        btn_register = root.findViewById(R.id.home_register_button);
        btn_login = root.findViewById(R.id.home_login_button);
        btn_logout = root.findViewById(R.id.home_logout_button);
        //crear una sessionManager
        session = new SessionManager(getActivity());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }

}