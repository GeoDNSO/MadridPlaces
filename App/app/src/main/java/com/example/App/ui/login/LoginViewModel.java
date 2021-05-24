package com.example.App.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.App.components.ViewModelParent;
import com.example.App.repositories.UserRepository;
import com.example.App.models.TUser;

public class LoginViewModel extends ViewModelParent {

    private UserRepository userRepository;
    private LiveData<TUser> mUser = new MutableLiveData<>();

    //observamos los objetos del repositorio, en este caso, el success devuelto por la llamada okhttp
    @Override
    public void init(){
        userRepository = new UserRepository();

        mSuccess = super.updateOnChange(userRepository.getmSuccess());
        mUser = super.updateOnChange(userRepository.getmUser());
    }

    //envia datos al servidor para registrar el nuevo usuario, en la primera linea se activa el progressBar
    public void login(String username, String pass){
        userRepository.loginUser(username, pass);
    }

    public LiveData<TUser> getUser(){
        return mUser;
    }
}