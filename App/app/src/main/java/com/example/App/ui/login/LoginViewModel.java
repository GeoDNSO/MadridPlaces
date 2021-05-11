package com.example.App.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.components.ViewModelParent;
import com.example.App.repositories.UserRepository;
import com.example.App.models.TUser;

public class LoginViewModel extends ViewModelParent {

    private UserRepository userRepository;
    private MutableLiveData<Boolean> mLoginInProcess = new MutableLiveData<>(); //true indica progress bar activo
    private LiveData<Boolean> mLoginSuccess = new MutableLiveData<>();
    private LiveData<TUser> mUser = new MutableLiveData<>();

    //observamos los objetos del repositorio, en este caso, el success devuelto por la llamada okhttp
    @Override
    public void init(){
        userRepository = new UserRepository();

        mLoginSuccess = super.updateOnChange(mLoginSuccess, userRepository.getmSuccess());
        mUser = super.updateOnChange(mUser, userRepository.getmUser());
    }

    //envia datos al servidor para registrar el nuevo usuario, en la primera linea se activa el progressBar
    public void login(String username, String pass){
        mLoginInProcess.setValue(true); //progress bar visible
        userRepository.loginUser(username, pass);
    }

    public LiveData<Boolean> getLoginSuccess(){
        return mLoginSuccess;
    }
    public LiveData<TUser> getUser(){
        return mUser;
    }
    public LiveData<Boolean> getLoginInProcess(){
        return mLoginInProcess;
    }
}