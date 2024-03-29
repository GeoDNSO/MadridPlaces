package com.example.App.ui.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.App.repositories.UserRepository;
import com.example.App.models.TUser;
import com.example.App.components.ViewModelParent;

import java.util.List;

public class AdminViewModel extends ViewModelParent {
    private UserRepository userRepository;
    private LiveData<List<TUser>> mListUsers = new MutableLiveData<>();

    //observamos los objetos del repositorio, en este caso, el success devuelto por la llamada okhttp
    public void init(){
        userRepository = new UserRepository();

        mSuccess = super.updateOnChange(userRepository.getmSuccess());
        mListUsers = super.updateOnChange(userRepository.getListUsers());

    }

    //TODO envia datos al servidor para registrar el nuevo usuario, en la primera linea se activa el progressBar
    public void listUsers(int page, int quantum, String searchText, int sort){
        userRepository.listUsers(page, quantum, searchText, sort);
    }

    public void clearList(){
        userRepository.clearListUsers();
    }

    public LiveData<List<TUser>> getListUsers(){
        return mListUsers;
    }
}