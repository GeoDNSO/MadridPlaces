package com.example.App.ui.profile;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.models.repositories.UserRepository;
import com.example.App.models.transfer.TUser;

public class ProfileViewModel extends ViewModel {

    UserRepository userRepository;
    private MutableLiveData<Boolean> mProfileActionInProgress = new MutableLiveData<>(); //true indica progress bar activo
    private LiveData<Integer> mActionProfileSuccess = new MutableLiveData<>();

    private LiveData<Pair<Integer, Integer>> mProfilePairMutableLiveData = new MutableLiveData<>();
    private LiveData<TUser> mUser = new MutableLiveData<>();

    public void init(){
        userRepository = new UserRepository();

        mActionProfileSuccess = Transformations.switchMap(
                userRepository.getProfileSuccess(),
                success -> setProfileActionInProgress(success));

        mProfilePairMutableLiveData = Transformations.switchMap(
                userRepository.getmCountProfileCommentsAndHistory(),
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

    public void countCommentsAndHistoryUser(String nickname) {
        mProfileActionInProgress.setValue(true);
        userRepository.getCommentsAndHistoryCount(nickname);
    }

    private LiveData<Pair<Integer, Integer>> setProfileActionInProgress(Pair<Integer, Integer> pair) {
        MutableLiveData<Pair<Integer, Integer>> mAux = new MutableLiveData<>();
        mAux.setValue(pair);
        return mAux;
    }

    //Para delete
    private LiveData<Integer> setProfileActionInProgress(Integer success) {
        mProfileActionInProgress.setValue(false); //progress bar visible
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }


    public LiveData<Boolean> getProfileActionInProgress(){
        return mProfileActionInProgress;
    }
    public LiveData<TUser> getUser(){
        return mUser;
    }
    public LiveData<Integer> getActionProfileSuccess(){
        return mActionProfileSuccess;
    }
    public LiveData<Pair<Integer, Integer>> getProfilePairMutableLiveData() {
        return mProfilePairMutableLiveData;
    }

}