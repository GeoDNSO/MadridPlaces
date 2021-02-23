package com.example.App.ui.DetailsProfileAdmin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.models.repositories.UserRepository;
import com.example.App.models.transfer.TUser;

public class DetailViewModel extends ViewModel {
    UserRepository userRepository;
    private MutableLiveData<Boolean> mDetailProfileActionInProgress = new MutableLiveData<>(); //true indica progress bar activo
    private LiveData<Integer> mActionDetailProfileSuccess = new MutableLiveData<>();
    private LiveData<TUser> mUser = new MutableLiveData<>();

    public void init(){
        userRepository = new UserRepository();

        mActionDetailProfileSuccess = Transformations.switchMap(
                userRepository.getProfileSuccess(),
                success -> setDetailProfileActionInProgress(success));

        mUser = Transformations.switchMap(
                userRepository.getmUser(),
                user -> setAndGetUser(user));

    }

    private LiveData<TUser> setAndGetUser(TUser user) {
        mDetailProfileActionInProgress.setValue(false); //progress bar visible
        MutableLiveData<TUser> mAux = new MutableLiveData<>();
        mAux.setValue(user);
        return mAux;
    }

    public void deleteUser(String username){
        mDetailProfileActionInProgress.setValue(true);
        userRepository.deleteUser(username);
    }

    //Para delete
    private LiveData<Integer> setDetailProfileActionInProgress(Integer success) {
        mDetailProfileActionInProgress.setValue(false); //progress bar visible
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }


    public LiveData<Boolean> getDetailProfileActionInProgress(){
        return mDetailProfileActionInProgress;
    }
    public LiveData<TUser> getUser(){
        return mUser;
    }
    public LiveData<Integer> getActionDetailProfileSuccess(){
        return mActionDetailProfileSuccess;
    }
}
