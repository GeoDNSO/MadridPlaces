package com.example.App.ui.details_profile_admin;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.repositories.UserRepository;
import com.example.App.models.TUser;

public class DetailViewModel extends ViewModel {
    UserRepository userRepository;
    private MutableLiveData<Boolean> mDetailProfileActionInProgress = new MutableLiveData<>(); //true indica progress bar activo
    private LiveData<Integer> mActionDetailProfileSuccess = new MutableLiveData<>();
    private LiveData<Pair<Integer, Integer>> mProfilePairMutableLiveData = new MutableLiveData<>();
    private LiveData<TUser> mUser = new MutableLiveData<>();

    public void init(){
        userRepository = new UserRepository();

        mActionDetailProfileSuccess = Transformations.switchMap(
                userRepository.getProfileSuccess(),
                success -> setDetailProfileActionInProgress(success));

        mUser = Transformations.switchMap(
                userRepository.getmUser(),
                user -> setAndGetUser(user));

        mProfilePairMutableLiveData = Transformations.switchMap(
                userRepository.getmCountProfileCommentsAndHistory(),
                success -> setProfileActionInProgress(success));

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

    private LiveData<Pair<Integer, Integer>> setProfileActionInProgress(Pair<Integer, Integer> pair) {
        MutableLiveData<Pair<Integer, Integer>> mAux = new MutableLiveData<>();
        mAux.setValue(pair);
        return mAux;
    }

    //Para delete
    private LiveData<Integer> setDetailProfileActionInProgress(Integer success) {
        mDetailProfileActionInProgress.setValue(false); //progress bar visible
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }

    public void countCommentsAndHistoryUser(String nickname) {
        mDetailProfileActionInProgress.setValue(true);
        userRepository.getCommentsAndHistoryCount(nickname);
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

    public LiveData<Pair<Integer, Integer>> getProfilePairMutableLiveData() {
        return mProfilePairMutableLiveData;
    }
}
