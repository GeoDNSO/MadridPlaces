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
import android.widget.Toast;

import com.example.App.App;
import com.example.App.R;
import com.example.App.transfer.TUser;
import com.example.App.utilities.Validator;

public class LoginFragment extends Fragment {

    private View root;
    private LoginViewModel mViewModel;

    /*MVVM*/
    private TextView tv_LoginText;
    private EditText et_Username, et_Password;
    private TextView tv_ToCreate;
    private Button loginButton;
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
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginOnClickAction(v);
            }
        });
        tv_ToCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });
    }

    private void initializeUI(){
        et_Username = (EditText) root.findViewById(R.id.username);
        et_Password = (EditText) root.findViewById(R.id.password);
        loginButton = (Button) root.findViewById(R.id.button);
        tv_ToCreate = (TextView) root.findViewById(R.id.login_to_create2);
        tv_LoginText = (TextView) root.findViewById(R.id.login_text);
    }

    private void loginOnClickAction(View v){
        String username = et_Username.getText().toString();
        String pass = et_Password.getText().toString();

        if (Validator.argumentsEmpty(username, pass)) {
            Toast.makeText(getActivity(), getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
        }

        if(true){ //TODO true --> Llamar a APP para loguear y actuar en consecuencia si el login ha salido bien o no
            //las primeras dos líneas de codigo son de ejemplo
            app = App.getInstance(getActivity());
            //TODO En vez de crear user por defecto hay que obtenerlo a traves de la app;
            TUser u = new TUser("JMorales", "xxxx","Juan", "Morales",
                    "juan@gmail.com", "H", "01/01/1990",
                    "Madrid", true);

            app.setUserSession(u);
            Toast.makeText(getActivity(), getString(R.string.sign_in), Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_homeFragment);
        }
        else{
            Toast.makeText(getActivity(), getString(R.string.register_failed), Toast.LENGTH_SHORT).show();
        }
    }

}