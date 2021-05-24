package com.example.App.ui.send_recommendation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.App.repositories.UserFriendRepository;
import com.example.App.repositories.RecommendationRepository;
import com.example.App.models.TRequestFriend;
import com.example.App.components.ViewModelParent;

import java.util.List;

public class SendRecommendationViewModel extends ViewModelParent {

    private RecommendationRepository userInteractionRepository;
    private UserFriendRepository userFriendRepository;

    private MutableLiveData<String> mSelectedItems = new MutableLiveData<>();
    private LiveData<List<TRequestFriend>> mFriendList = new MutableLiveData<>();

    @Override
    public void init() {
        userInteractionRepository = new RecommendationRepository();
        userFriendRepository = new UserFriendRepository();

        mSuccess  = super.updateOnChange(userInteractionRepository.getSuccess());
        mFriendList = super.updateOnChange(userFriendRepository.getmFriendList());
    }

    public void friendList(String username) {
        userFriendRepository.friendList(username);
    }


    public void sendRecomendation(String userOrigin, String userDest, String place) {
        userInteractionRepository.sendRecommendation(userOrigin, userDest, place);
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