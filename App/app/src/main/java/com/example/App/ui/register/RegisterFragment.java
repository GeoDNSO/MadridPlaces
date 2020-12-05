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
    private EditText et_Name;
    private EditText et_Surname;
    private EditText et_Username;
    private EditText et_Email;
    private EditText et_Password, et_RepeatPassword;
    private Button registerButton;
    private TextView tv_ToLogin;



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
        et_Name = (EditText) root.findViewById(R.id.register_completename);
        et_Username = (EditText) root.findViewById(R.id.username);
        et_Email = (EditText) root.findViewById(R.id.register_email);
        et_Password = (EditText) root.findViewById(R.id.password);
        et_RepeatPassword = (EditText) root.findViewById(R.id.register_repeat_password);
        registerButton = (Button) root.findViewById(R.id.button);
        tv_ToLogin = (TextView) root.findViewById(R.id.to_login);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        // TODO: Use the ViewModel
    }


    private void initializeListeners(){
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerOnClickAction(v);
            }
        });

        tv_ToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_loginFragment);
            }
        });
    }


    private void registerOnClickAction(View v){
        String username = et_Username.getText().toString();
        String name = et_Name.getText().toString();
        String surname = et_Surname.getText().toString();
        String pass = et_Password.getText().toString();
        String pass2 = et_RepeatPassword.getText().toString();
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

}