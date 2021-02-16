package com.example.App.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.models.dao.DAOUserImp;
import com.example.App.models.transfer.TUser;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<TUser> mTUser;
    private DAOUserImp repositorio;
    private MutableLiveData<Boolean> mIsDoingLogin = new MutableLiveData<>(); //true indica progress bar activo
    private LiveData<Boolean> mIsDoneLogin = new MutableLiveData<>();
    private LiveData<TUser> mUser = new MutableLiveData<>();

    //funcion que se usa en el switchMap, asocia un liveData cuando cambia el valor de mSuccess en DAOUserImp
    private LiveData<Boolean> getNuevoIsDone(Boolean success) {
        mIsDoingLogin.setValue(false); //progress bar visible
        MutableLiveData<Boolean> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }

    private LiveData<TUser> getNuevoUser(TUser user) {
        mIsDoingLogin.setValue(false); //progress bar visible
        MutableLiveData<TUser> mAux = new MutableLiveData<>();
        mAux.setValue(user);
        return mAux;
    }

    //observamos los objetos del repositorio, en este caso, el success devuelto por la llamada okhttp
    public void init(){
        //repositorio = DAOUserImp.getInstance();
        repositorio = new DAOUserImp();

        mIsDoneLogin = Transformations.switchMap(
                repositorio.getSuccess(),
                success -> getNuevoIsDone(success));

        mUser = Transformations.switchMap(
                repositorio.getUser(),
                user -> getNuevoUser(user));


    }

    //devuelve el liveData para poder observarlo desde la vista
    public LiveData<Boolean> getIsDoneLogin(){
        return mIsDoneLogin;
    }
    public LiveData<TUser> getUser(){
        return mUser;
    }
    //devuelve el liveData para poder observarlo desde la vista
    public LiveData<Boolean> getIsDoingLogin(){
        return mIsDoingLogin;
    }


    //envia datos al servidor para registrar el nuevo usuario, en la primera linea se activa el progressBar
    public void login(String username, String pass){
        mIsDoingLogin.setValue(true); //progress bar visible
        repositorio.login(username, pass);
    }
}