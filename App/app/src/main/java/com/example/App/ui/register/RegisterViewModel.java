package com.example.App.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.components.ViewModelParent;
import com.example.App.repositories.UserRepository;
import com.example.App.models.TUser;

public class RegisterViewModel extends ViewModelParent {

    private UserRepository userRepository;
    private MutableLiveData<Boolean> mRegisterInProcess = new MutableLiveData<>(); //true indica progress bar activo
    private LiveData<Boolean> mRegisterSuccess = new MutableLiveData<>();

    //observamos los objetos del repositorio, en este caso, el success devuelto por la llamada okhttp
    @Override
    public void init(){
        userRepository = new UserRepository();

        mRegisterSuccess = super.updateOnChange(mRegisterSuccess, userRepository.getmSuccess());
    }

    //envia datos al servidor para registrar el nuevo usuario, en la primera linea se activa el progressBar
    public void registerUser(String username, String pass, String name, String surname, String email, String gender, String birthDate, String city, String rol, String profileImage){
        mRegisterInProcess.setValue(true); //progress bar visible
        TUser user = new TUser(username, pass, name, surname, email, gender, birthDate, city, rol, profileImage);
        userRepository.registerUser(user);
    }

    //devuelve el liveData para poder observarlo desde la vista
    public LiveData<Boolean> getIsDoneRegistration(){
        return mRegisterSuccess;
    }

    //devuelve el liveData para poder observarlo desde la vista
    public LiveData<Boolean> getRegisterInProcess(){
        return mRegisterInProcess;
    }

}