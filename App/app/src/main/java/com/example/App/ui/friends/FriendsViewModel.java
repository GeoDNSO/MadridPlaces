package com.example.App.ui.friends;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.App.repositories.UserFriendRepository;
import com.example.App.models.TRequestFriend;
import com.example.App.ui.ViewModelParent;

import java.util.List;

public class FriendsViewModel extends ViewModelParent {

    private UserFriendRepository friendRepository;

    private LiveData<List<TRequestFriend>> mFriendRequestList = new MutableLiveData<>();
    private LiveData<List<TRequestFriend>> mFriendList = new MutableLiveData<>();
    private LiveData<Integer> mAcceptFriend = new MutableLiveData<>();
    private LiveData<Integer> mDeclineFriend = new MutableLiveData<>();
    private LiveData<Integer> mFriendRequest = new MutableLiveData<>();
    private LiveData<Integer> mDeleteFriend = new MutableLiveData<>();
    private LiveData<Integer> mSendRequestFriend = new MutableLiveData<>();

    public void init() {
        friendRepository = new UserFriendRepository();

        mFriendList = Transformations.switchMap(
                friendRepository.getmFriendList(),
                listFriend -> setFriendList(listFriend)
        );
        mFriendRequestList = Transformations.switchMap(
                friendRepository.getmFriendRequestList(),
                listFriend -> setFriendRequestList(listFriend)
        );
        mAcceptFriend = Transformations.switchMap(
                friendRepository.getmAcceptFriend(),
                acceptRecom -> setAcceptFriend(acceptRecom)
        );
        mDeclineFriend = Transformations.switchMap(
                friendRepository.getmDeclineFriend(),
                denyRecom -> setDeclineFriend(denyRecom)
        );
        mFriendRequest = Transformations.switchMap(
                friendRepository.getmFriendRequest(),
                requestFriend -> setDeclineFriend(requestFriend)
        );
        mDeleteFriend = Transformations.switchMap(
                friendRepository.getmDeleteFriend(),
                deleteFriend -> setDeleteFriend(deleteFriend)
        );
        mSendRequestFriend = Transformations.switchMap(
                friendRepository.getmFriendRequest(),
                sendRequestFriend -> setSendRequestFriend(sendRequestFriend)
        );
    }

    private LiveData<Integer> setSendRequestFriend(Integer sendRequestFriend) {
        mProgressBar.setValue(false);
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(sendRequestFriend);
        return mAux;
    }

    private LiveData<Integer> setDeleteFriend(Integer deleteFriend) {
        mProgressBar.setValue(false);
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(deleteFriend);
        return mAux;
    }

    private LiveData<List<TRequestFriend>> setFriendRequestList(List<TRequestFriend> listFriend) {
        mProgressBar.setValue(false);
        MutableLiveData<List<TRequestFriend>> mAux = new MutableLiveData<>();
        mAux.setValue(listFriend);
        return mAux;
    }

    private LiveData<List<TRequestFriend>> setFriendList(List<TRequestFriend> listFriend) {
        mProgressBar.setValue(false);
        MutableLiveData<List<TRequestFriend>> mAux = new MutableLiveData<>();
        mAux.setValue(listFriend);
        return mAux;
    }

    private LiveData<Integer> setDeclineFriend(Integer denyRecom) {
        mProgressBar.setValue(false);
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(denyRecom);
        return mAux;
    }

    private LiveData<Integer> setRequestFriend(Integer requestFriend) {
        mProgressBar.setValue(false);
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(requestFriend);
        return mAux;
    }

    private LiveData<Integer> setAcceptFriend(Integer acceptRecom) {
        mProgressBar.setValue(false);
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(acceptRecom);
        return mAux;
    }

    public void declineFriendRequest(String userOrigin, String userDest) {
        friendRepository.declineFriendRequest(userOrigin,userDest);
    }

    public void acceptFriendRequest(String userOrigin, String userDest) {
        friendRepository.acceptFriendRequest(userOrigin,userDest);
    }

    public void deleteFriend(String userToDelete, String currentUser) {
        friendRepository.deleteFriend(userToDelete, currentUser);
    }

    public void friendRequestList(String username) {
        friendRepository.friendRequestList(username);
    }

    public void friendList(String username) {
        friendRepository.friendList(username);
    }


    public LiveData<Integer> getmDeclineFriend() {
        return mDeclineFriend;
    }

    public LiveData<Integer> getmAcceptFriend() {
        return mAcceptFriend;
    }

    public LiveData<List<TRequestFriend>> getmFriendRequestList() {
        return mFriendRequestList;
    }

    public LiveData<Integer> getmFriendRequest() {
        return mFriendRequest;
    }

    public LiveData<Integer> getmDeleteFriend() {
        return mDeleteFriend;
    }

    public LiveData<Integer> getmSendRequestFriend() {
        return mSendRequestFriend;
    }

    public void sendFriendRequest(String username) {
        friendRepository.sendFriendRequest(username);
    }

    public LiveData<List<TRequestFriend>> getmFriendList() {
        return mFriendList;
    }
}