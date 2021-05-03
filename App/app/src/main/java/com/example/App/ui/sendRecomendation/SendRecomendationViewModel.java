package com.example.App.ui.sendRecomendation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.App.models.repositories.UserInteractionRepository;
import com.example.App.ui.ViewModelParent;

public class SendRecomendationViewModel extends ViewModelParent {

    private UserInteractionRepository userInteractionRepository;
    private MutableLiveData<Boolean> mSendingInProcess = new MutableLiveData<>();
    private LiveData<Integer> mSendingSuccess = new MutableLiveData<>();
    private MutableLiveData<String> mSelectedItems = new MutableLiveData<>();

    @Override
    public void init() {
        userInteractionRepository = new UserInteractionRepository();

        mSendingSuccess = Transformations.switchMap(
                userInteractionRepository.getSuccess(),
                success -> setSendingSuccess(success)
        );
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
}