package com.example.App.ui.login;

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
import android.widget.EditText;
import android.widget.TextView;

import com.example.App.App;
import com.example.App.R;
import com.example.App.SessionManager;

public class LoginFragment extends Fragment {

    private View root;
    private LoginViewModel mViewModel;

    /*MVVM*/
    private EditText username, password;
    private TextView to_create;
    private Button login;
    private App app; //global variable

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.login_fragment, container, false);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        initializeUI();
        initializeListeners();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initializeListeners(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginOnClickAction(v);
            }
        });
        to_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });
    }

    private void initializeUI(){
        username = (EditText) root.findViewById(R.id.username);
        password = (EditText) root.findViewById(R.id.password);
        login = (Button) root.findViewById(R.id.button);
        to_create = (TextView) root.findViewById(R.id.login_to_create2);
    }

    private void loginOnClickAction(View v){
        //De ejemplo
        app = App.getInstance(getActivity());
        app.setUsername("Hola");
        Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_homeFragment);
    }

}