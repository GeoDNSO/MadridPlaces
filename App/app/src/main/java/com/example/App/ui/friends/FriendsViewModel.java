package com.example.App.ui.friends;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.App.repositories.UserFriendRepository;
import com.example.App.models.TRequestFriend;
import com.example.App.components.ViewModelParent;

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

        mFriendList = super.updateOnChange(mFriendList, friendRepository.getmFriendList());

        mFriendList = super.updateOnChange(mFriendList, friendRepository.getmFriendList());
        mFriendRequestList = super.updateOnChange(mFriendRequestList, friendRepository.getmFriendRequestList());
        mAcceptFriend = super.updateOnChange(mAcceptFriend, friendRepository.getmAcceptFriend());
        mDeclineFriend = super.updateOnChange(mDeclineFriend, friendRepository.getmDeclineFriend());
        mFriendRequest = super.updateOnChange(mFriendRequest, friendRepository.getmFriendRequest());
        mDeleteFriend = super.updateOnChange(mDeleteFriend, friendRepository.getmDeleteFriend());
        mSendRequestFriend = super.updateOnChange(mSendRequestFriend, friendRepository.getmFriendRequest());
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