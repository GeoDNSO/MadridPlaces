package com.example.App.ui.profile;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.App.components.ViewModelParent;
import com.example.App.repositories.UserRepository;
import com.example.App.models.TUser;

public class ProfileViewModel extends ViewModelParent {

    private UserRepository userRepository;
    private MutableLiveData<Boolean> mProfileActionInProgress = new MutableLiveData<>(); //true indica progress bar activo

    private LiveData<Pair<Integer, Integer>> mProfilePairMutableLiveData = new MutableLiveData<>();
    private LiveData<TUser> mUser = new MutableLiveData<>();

    @Override
    public void init(){
        userRepository = new UserRepository();

        mSuccess = super.updateOnChange(userRepository.getmSuccess());
        mProfilePairMutableLiveData = super.updateOnChange(userRepository.getmCountProfileCommentsAndHistory());
        mUser = super.updateOnChange(userRepository.getmUser());
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

    public LiveData<TUser> getUser(){
        return mUser;
    }

    public LiveData<Pair<Integer, Integer>> getProfilePairMutableLiveData() {
        return mProfilePairMutableLiveData;
    }
}