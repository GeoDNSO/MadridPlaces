package com.example.App.ui.details_profile_admin;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.components.ViewModelParent;
import com.example.App.repositories.UserRepository;
import com.example.App.models.TUser;

public class DetailViewModel extends ViewModelParent {
    UserRepository userRepository;
    private MutableLiveData<Boolean> mDetailProfileActionInProgress = new MutableLiveData<>(); //true indica progress bar activo
    private LiveData<Integer> mActionDetailProfileSuccess = new MutableLiveData<>();
    private LiveData<Pair<Integer, Integer>> mProfilePairMutableLiveData = new MutableLiveData<>();
    private LiveData<TUser> mUser = new MutableLiveData<>();

    @Override
    public void init(){
        userRepository = new UserRepository();

        mSuccess = super.updateOnChange(mSuccess, userRepository.getmSuccess());
        mProfilePairMutableLiveData = super.updateOnChange(mProfilePairMutableLiveData, userRepository.getmCountProfileCommentsAndHistory());

        mUser = super.updateOnChange(mUser, userRepository.getmUser());
    }

    public void deleteUser(String username){
        mDetailProfileActionInProgress.setValue(true);
        userRepository.deleteUser(username);
    }

    public void countCommentsAndHistoryUser(String nickname) {
        mDetailProfileActionInProgress.setValue(true);
        userRepository.getCommentsAndHistoryCount(nickname);
    }

    public LiveData<TUser> getUser(){
        return mUser;
    }
    public LiveData<Pair<Integer, Integer>> getProfilePairMutableLiveData() {
        return mProfilePairMutableLiveData;
    }
}
