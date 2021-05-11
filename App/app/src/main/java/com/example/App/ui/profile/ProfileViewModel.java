package com.example.App.ui.profile;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.components.ViewModelParent;
import com.example.App.repositories.UserRepository;
import com.example.App.models.TUser;

public class ProfileViewModel extends ViewModelParent {

    private UserRepository userRepository;
    private MutableLiveData<Boolean> mProfileActionInProgress = new MutableLiveData<>(); //true indica progress bar activo
    private LiveData<Integer> mActionProfileSuccess = new MutableLiveData<>();

    private LiveData<Pair<Integer, Integer>> mProfilePairMutableLiveData = new MutableLiveData<>();
    private LiveData<TUser> mUser = new MutableLiveData<>();

    @Override
    public void init(){
        userRepository = new UserRepository();

        mActionProfileSuccess = super.updateOnChange(mActionProfileSuccess, userRepository.getProfileSuccess());
        mProfilePairMutableLiveData = super.updateOnChange(mProfilePairMutableLiveData, userRepository.getmCountProfileCommentsAndHistory());
        mUser = super.updateOnChange(mUser, userRepository.getmUser());
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