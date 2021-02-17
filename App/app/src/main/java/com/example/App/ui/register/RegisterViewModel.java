package com.example.App.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.models.repositories.UserRepository;
import com.example.App.models.transfer.TUser;

public class RegisterViewModel extends ViewModel {

    private UserRepository userRepository;
    private MutableLiveData<Boolean> mRegisterInProcess = new MutableLiveData<>(); //true indica progress bar activo
    private LiveData<Boolean> mRegisterSuccess = new MutableLiveData<>();

    //observamos los objetos del repositorio, en este caso, el success devuelto por la llamada okhttp
    public void init(){
        userRepository = new UserRepository();
        mRegisterSuccess = Transformations.switchMap(
                userRepository.getmSuccess(),
                success -> setRegisterInProcess(success));

    }

    //envia datos al servidor para registrar el nuevo usuario, en la primera linea se activa el progressBar
    public void registerUser(String username, String pass, String name, String surname, String email, String gender, String birthDate, String city, String rol){
        mRegisterInProcess.setValue(true); //progress bar visible
        TUser user = new TUser(username, pass, name, surname, email, gender, birthDate, city, rol);
        userRepository.registerUser(user);
    }

    //TODO puede ser void??
    //funcion que se usa en el switchMap, asocia un liveData cuando cambia el valor de mSuccess en DAOUserImp
    private LiveData<Boolean> setRegisterInProcess(Boolean success) {
        mRegisterInProcess.setValue(false); //progress bar visible
        MutableLiveData<Boolean> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
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