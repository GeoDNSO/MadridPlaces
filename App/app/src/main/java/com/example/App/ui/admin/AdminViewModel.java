package com.example.App.ui.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.models.repositories.UserRepository;
import com.example.App.models.transfer.TUser;
import com.example.App.ui.ViewModelParent;

import java.util.List;

public class AdminViewModel extends ViewModelParent {
    private UserRepository userRepository;
    private LiveData<Integer> mListSuccess = new MutableLiveData<>();
    private LiveData<List<TUser>> mListUsers = new MutableLiveData<>();

    //observamos los objetos del repositorio, en este caso, el success devuelto por la llamada okhttp
    public void init(){
        userRepository = new UserRepository();

        mListSuccess = Transformations.switchMap(
                userRepository.getProfileSuccess(),
                success -> getListInProcess(success));

        mListUsers = Transformations.switchMap(
                userRepository.getListUsers(),
                user -> setAndGetListUsers(user));
    }

    //TODO envia datos al servidor para registrar el nuevo usuario, en la primera linea se activa el progressBar
    public void listUsers(int page, int quantum){
        userRepository.listUsers(page, quantum);
    }

    //funcion que se usa en el switchMap, asocia un liveData cuando cambia el valor de mSuccess en DAOUserImp
    private LiveData<Integer> getListInProcess(Integer success) {
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }

    private LiveData<List<TUser>> setAndGetListUsers(List<TUser> user) {
        MutableLiveData<List<TUser>> mAux = new MutableLiveData<>();
        mAux.setValue(user);
        return mAux;
    }

    public LiveData<Integer> getListSuccess(){
        return mListSuccess;
    }
    public LiveData<List<TUser>> getListUsers(){
        return mListUsers;
    }
}