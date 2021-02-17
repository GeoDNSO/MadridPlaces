package com.example.App.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.models.repositories.UserRepository;
import com.example.App.models.transfer.TUser;

public class ProfileViewModel extends ViewModel {

    UserRepository userRepository;
    private MutableLiveData<Boolean> mProfileActionInProgress = new MutableLiveData<>(); //true indica progress bar activo
    private LiveData<Boolean> mDeleteProfileSuccess = new MutableLiveData<>();
    private LiveData<Boolean> mModifyProfileSuccess = new MutableLiveData<>();
    private LiveData<TUser> mUser = new MutableLiveData<>();

    public void init(){
        userRepository = new UserRepository();

        mDeleteProfileSuccess = Transformations.switchMap(
                userRepository.getmSuccess(),
                success -> setProfileActionInProgress(success));

        mModifyProfileSuccess = Transformations.switchMap(
                userRepository.getmSuccess(),
                success -> setProfileActionInProgress(success));

        mUser = Transformations.switchMap(
                userRepository.getmUser(),
                user -> setAndGetUser(user));

    }

    private LiveData<TUser> setAndGetUser(TUser user) {
        mProfileActionInProgress.setValue(false); //progress bar visible
        MutableLiveData<TUser> mAux = new MutableLiveData<>();
        mAux.setValue(user);
        return mAux;
    }

    public void deleteUser(String username){
        mProfileActionInProgress.setValue(true);
        userRepository.deleteUser(username);
    }

    public void modifyUser(TUser u) {
        mProfileActionInProgress.setValue(true);
        userRepository.modifyUser(u);
    }

    //Se puede usar tanto para modificar como para actualizar
    private LiveData<Boolean> setProfileActionInProgress(Boolean success) {
        mProfileActionInProgress.setValue(false); //progress bar visible
        MutableLiveData<Boolean> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }

    public LiveData<Boolean> getDeleteProfileSuccess(){
        return mDeleteProfileSuccess;
    }
    public LiveData<Boolean> getProfileActionInProgress(){
        return mProfileActionInProgress;
    }
    public LiveData<TUser> getUser(){
        return mUser;
    }
    public LiveData<Boolean> getModifyProfileSuccess(){
        return mModifyProfileSuccess;
    }

}