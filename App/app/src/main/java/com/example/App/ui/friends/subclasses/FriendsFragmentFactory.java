package com.example.App.ui.friends.subclasses;

import androidx.fragment.app.Fragment;

import com.example.App.App;
import com.example.App.R;
import com.example.App.ui.friends.subclasses.friend_resquest_list.FriendRequestListFragment;
import com.example.App.ui.friends.subclasses.friends_list.FriendListFragment;

public class FriendsFragmentFactory {

    public Fragment getInstance(String type){
        App app = App.getInstance();

        String friendList = app.getAppString(R.string.friend_list);
        String friendRequest = app.getAppString(R.string.friend_request);

        if(type.equals(friendList)){
            return new FriendListFragment();
        }
        if(type.equals(friendRequest)){
            return new FriendRequestListFragment();
        }
        return null;
    }
}
