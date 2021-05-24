package com.example.App.ui.register;

import com.example.App.components.ViewModelParent;
import com.example.App.repositories.UserRepository;
import com.example.App.models.TUser;

public class RegisterViewModel extends ViewModelParent {

    private UserRepository userRepository;

    //observamos los objetos del repositorio, en este caso, el success devuelto por la llamada okhttp
    @Override
    public void init(){
        userRepository = new UserRepository();
        mSuccess = super.updateOnChange(userRepository.getmSuccess());
    }

    //envia datos al servidor para registrar el nuevo usuario, en la primera linea se activa el progressBar
    public void registerUser(String username, String pass, String name, String surname, String email, String gender, String birthDate, String city, String rol, String profileImage){
        TUser user = new TUser(username, pass, name, surname, email, gender, birthDate, city, rol, profileImage);
        userRepository.registerUser(user);
    }

}