package com.example.App.ui.register;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.App.models.dao.DAOUserImp;
import com.example.App.models.transfer.TUser;

import java.util.List;

public class RegisterViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<TUser> mTUser;
    private DAOUserImp repositorio;
    private MutableLiveData<Boolean> mIsDoingRegistration = new MutableLiveData<>(); //true indica progress bar activo
    public void init(){
        repositorio = DAOUserImp.getInstance();
    }
    public LiveData<TUser> getTUser(){
        return mTUser;
    }

    public LiveData<Boolean> getIsDoingRegistration(){
        return mIsDoingRegistration;
    }


    //los cambios se notifican al terminar la funcion, separar inicio y fin de progress value con tarea asincrona
    public boolean registrar(String username, String pass, String name, String surname, String email, String gender, String birthDate, String city, String rol){
        mIsDoingRegistration.setValue(true);
        //mIsDoingRegistration.setValue(true); //progress bar visible
        TUser user = new TUser(username, pass, name, surname, email, gender, birthDate, city, rol);
        //mTUser.setValue(user);
        boolean registro = repositorio.registerObject(user);

        Log.d("valor registro", String.valueOf(registro));
        //mIsDoingRegistration.setValue(false);//progress bar gone

        //mIsDoingRegistration.setValue(false); //progress bar gone

        return registro;
    }

}