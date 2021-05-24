package com.example.App.ui.friends;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.App.repositories.UserFriendRepository;
import com.example.App.models.TRequestFriend;
import com.example.App.components.ViewModelParent;

import java.util.List;

public class FriendsViewModel extends ViewModelParent {

    private UserFriendRepository friendRepository;

    private LiveData<List<TRequestFriend>> mFriendRequestList = new MutableLiveData<>();
    private LiveData<List<TRequestFriend>> mFriendList = new MutableLiveData<>();

    public void init() {
        friendRepository = new UserFriendRepository();

        mFriendList = super.updateOnChange(friendRepository.getmFriendList());

        mSuccess = super.updateOnChange(friendRepository.getSuccess());

        mFriendList = super.updateOnChange(friendRepository.getmFriendList());
        mFriendRequestList = super.updateOnChange(friendRepository.getmFriendRequestList());
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

    public void sendFriendRequest(String username) {
        friendRepository.sendFriendRequest(username);
    }

    public LiveData<List<TRequestFriend>> getmFriendRequestList() {
        return mFriendRequestList;
    }
    public LiveData<List<TRequestFriend>> getmFriendList() {
        return mFriendList;
    }
}