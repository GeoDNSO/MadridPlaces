package com.example.App.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.models.dao.DAOUserImp;
import com.example.App.models.transfer.TUser;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<TUser> mTUser;
    private DAOUserImp repositorio;
    private MutableLiveData<Boolean> mIsDoingRegistration = new MutableLiveData<>(); //true indica progress bar activo
    private LiveData<Boolean> mIsDoneRegistration = new MutableLiveData<>();

    //funcion que se usa en el switchMap, asocia un liveData cuando cambia el valor de mSuccess en DAOUserImp
    private LiveData<Boolean> getNuevoIsDone(Boolean success) {
        mIsDoingRegistration.setValue(false); //progress bar visible
        MutableLiveData<Boolean> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }

    //observamos los objetos del repositorio, en este caso, el success devuelto por la llamada okhttp
    public void init(){
        repositorio = DAOUserImp.getInstance();

        mIsDoneRegistration = Transformations.switchMap(
                repositorio.getSuccess(),
                success -> getNuevoIsDone(success));


    }

    //devuelve el liveData para poder observarlo desde la vista
    public LiveData<Boolean> getIsDoneRegistration(){
        return mIsDoneRegistration;
    }

    //devuelve el liveData para poder observarlo desde la vista (es de prueba)
    //TODO: Eliminar cuando lo hayan entendido todos
    public LiveData<TUser> getTUser(){
        return mTUser;
    }

    //devuelve el liveData para poder observarlo desde la vista
    public LiveData<Boolean> getIsDoingRegistration(){
        return mIsDoingRegistration;
    }


    //envia datos al servidor para registrar el nuevo usuario, en la primera linea se activa el progressBar
    public void registrar(String username, String pass, String name, String surname, String email, String gender, String birthDate, String city, String rol){
        mIsDoingRegistration.setValue(true); //progress bar visible
        TUser user = new TUser(username, pass, name, surname, email, gender, birthDate, city, rol);

        repositorio.registerObject(user);
    }

}