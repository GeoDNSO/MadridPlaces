package com.example.App.ui.register;

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
import android.widget.Toast;

import com.example.App.R;

public class RegisterFragment extends Fragment {

    private View root;
    private RegisterViewModel mViewModel;

    /*MVVM*/
    private EditText completeName;
    private EditText username;
    private EditText email;
    private EditText password, repeatpassword;
    private Button register;
    private TextView to_login;



    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.register_fragment, container, false);
        mViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        initializeUI();
        initializeListeners();

        return root;
    }

    private void initializeUI(){
        completeName = (EditText) root.findViewById(R.id.register_completename);
        username = (EditText) root.findViewById(R.id.username);
        email = (EditText) root.findViewById(R.id.register_email);
        password = (EditText) root.findViewById(R.id.password);
        repeatpassword = (EditText) root.findViewById(R.id.register_repeat_password);
        register = (Button) root.findViewById(R.id.button);
        to_login = (TextView) root.findViewById(R.id.to_login);
    }

    private void initializeListeners(){
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = password.getText().toString();
                String pass2 = repeatpassword.getText().toString();
                if(!pass.isEmpty()){
                    if(pass.equals(pass2)) {
                        Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_homeFragment);
                    }
                    else{
                        Toast.makeText(getActivity(), "Contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(), "Contraseña vacía", Toast.LENGTH_SHORT).show();
                }
            }
        });

        to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_loginFragment);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        // TODO: Use the ViewModel
    }

}