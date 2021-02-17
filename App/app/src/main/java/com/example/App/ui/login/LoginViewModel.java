package com.example.App.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.models.repositories.UserRepository;
import com.example.App.models.transfer.TUser;

public class LoginViewModel extends ViewModel {

    private UserRepository userRepository;
    private MutableLiveData<Boolean> mLoginInProcess = new MutableLiveData<>(); //true indica progress bar activo
    private LiveData<Boolean> mLoginSuccess = new MutableLiveData<>();
    private LiveData<TUser> mUser = new MutableLiveData<>();

    //observamos los objetos del repositorio, en este caso, el success devuelto por la llamada okhttp
    public void init(){
        userRepository = new UserRepository();

        mLoginSuccess = Transformations.switchMap(
                userRepository.getmSuccess(),
                success -> getLoginInProcess(success));

        mUser = Transformations.switchMap(
                userRepository.getmUser(),
                user -> setAndGetUser(user));
    }

    //envia datos al servidor para registrar el nuevo usuario, en la primera linea se activa el progressBar
    public void login(String username, String pass){
        mLoginInProcess.setValue(true); //progress bar visible
        userRepository.loginUser(username, pass);
    }

    //funcion que se usa en el switchMap, asocia un liveData cuando cambia el valor de mSuccess en DAOUserImp
    private LiveData<Boolean> getLoginInProcess(Boolean success) {
        mLoginInProcess.setValue(false); //progress bar visible
        MutableLiveData<Boolean> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }

    private LiveData<TUser> setAndGetUser(TUser user) {
        mLoginInProcess.setValue(false); //progress bar visible
        MutableLiveData<TUser> mAux = new MutableLiveData<>();
        mAux.setValue(user);
        return mAux;
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