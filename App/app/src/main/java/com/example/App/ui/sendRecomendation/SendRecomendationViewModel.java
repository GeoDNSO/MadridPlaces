package com.example.App.ui.sendRecomendation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.App.repositories.UserFriendRepository;
import com.example.App.repositories.UserInteractionRepository;
import com.example.App.models.TRequestFriend;
import com.example.App.ui.ViewModelParent;

import java.util.List;

public class SendRecomendationViewModel extends ViewModelParent {

    private UserInteractionRepository userInteractionRepository;
    private UserFriendRepository userFriendRepository;
    private MutableLiveData<Boolean> mSendingInProcess = new MutableLiveData<>();
    private LiveData<Integer> mSendingSuccess = new MutableLiveData<>();
    private MutableLiveData<String> mSelectedItems = new MutableLiveData<>();
    private LiveData<List<TRequestFriend>> mFriendList = new MutableLiveData<>();

    @Override
    public void init() {
        userInteractionRepository = new UserInteractionRepository();
        userFriendRepository = new UserFriendRepository();

        mSendingSuccess = Transformations.switchMap(
                userInteractionRepository.getSuccess(),
                success -> setSendingSuccess(success)
        );

        mFriendList = Transformations.switchMap(
                userFriendRepository.getmFriendList(),
                listFriend -> setFriendList(listFriend)
        );
    }

    public void friendList(String username) {
        userFriendRepository.friendList(username);
    }

    private LiveData<List<TRequestFriend>> setFriendList(List<TRequestFriend> listFriend) {
        mProgressBar.setValue(false);
        MutableLiveData<List<TRequestFriend>> mAux = new MutableLiveData<>();
        mAux.setValue(listFriend);
        return mAux;
    }

    private LiveData<Integer> setSendingSuccess(Integer success) {
        mSendingInProcess.setValue(false);
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }


    public void sendRecomendation(String userOrigin, String userDest, String place) {
        mSendingInProcess.setValue(true);
        userInteractionRepository.sendRecomendation(userOrigin, userDest, place);
    }

    public LiveData<Boolean> getSendingInProgress() {
        return mSendingInProcess;
    }

    public LiveData<Integer> getSendingSuccess() {
        return mSendingSuccess;
    }

    public MutableLiveData<String> getmSelectedItems() {
        return mSelectedItems;
    }

    public void setmSelectedItems(String s) {
        mSelectedItems.setValue(s);
    }

    public LiveData<List<TRequestFriend>> getmFriendList() {
        return mFriendList;
    }
}