package com.example.App.ui.recommendations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.App.repositories.UserInteractionRepository;
import com.example.App.models.TRecommendation;
import com.example.App.components.ViewModelParent;

import java.util.List;

public class RecommendationsViewModel extends ViewModelParent {
    private LiveData<List<TRecommendation>> mListRecom = new MutableLiveData<>();
    private LiveData<List<TRecommendation>> mListPendingRecom = new MutableLiveData<>();
    private LiveData<Integer> mAcceptRecom = new MutableLiveData<>();
    private LiveData<Integer> mDenyRecom = new MutableLiveData<>();

    private UserInteractionRepository recomRepository;

    @Override
    public void init() {
        recomRepository = new UserInteractionRepository();
        mListRecom = Transformations.switchMap(
                recomRepository.getmRecommendationsList(),
                listRecom -> setListRecom(listRecom)
        );
        mListPendingRecom = Transformations.switchMap(
                recomRepository.getmPendingRecommendationsList(),
                listPendingRecom -> setListPendingRecom(listPendingRecom)
        );
        mAcceptRecom = Transformations.switchMap(
                recomRepository.getmAcceptRecommendation(),
                acceptRecom -> setAcceptRecom(acceptRecom)
        );
        mDenyRecom = Transformations.switchMap(
                recomRepository.getmDenyRecommendation(),
                denyRecom -> setDenyRecom(denyRecom)
        );
    }

    public void listUserRecommendations(int page,int quant, String username){
        recomRepository.listRecom(page, quant, username);
    }

    private LiveData<List<TRecommendation>> setListRecom(List<TRecommendation> listRecom){
        mlv_isLoading.setValue(false);
        MutableLiveData<List<TRecommendation>> mAux = new MutableLiveData<>();
        mAux.setValue(listRecom);
        return mAux;
    }

    private LiveData<List<TRecommendation>> setListPendingRecom(List<TRecommendation> listPendingRecom){
        mlv_isLoading.setValue(false);
        MutableLiveData<List<TRecommendation>> mAux = new MutableLiveData<>();
        mAux.setValue(listPendingRecom);
        return mAux;
    }

    private LiveData<Integer> setAcceptRecom(int listPendingRecom){
        mlv_isLoading.setValue(false);
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(listPendingRecom);
        return mAux;
    }

    private LiveData<Integer> setDenyRecom(int listPendingRecom){
        mlv_isLoading.setValue(false);
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(listPendingRecom);
        return mAux;
    }

    public void setmListRecom(LiveData<List<TRecommendation>> mListRecom) {
        this.mListRecom = mListRecom;
    }

    public void listUserPendingRecommendations(int page, int quantum, String username) {
        recomRepository.listPendingRecom(page, quantum, username);
    }

    public void acceptPendingRecommendation(String placeName, String userOrigin, String userDest){
        recomRepository.acceptPendingRecom(placeName, userOrigin, userDest);
    }

    public void denyPendingRecommendation(String placeName, String userOrigin, String userDest){
        recomRepository.denyPendingRecom(placeName, userOrigin, userDest);
    }

    public LiveData<List<TRecommendation>> getmListRecom() {
        return mListRecom;
    }
    public LiveData<List<TRecommendation>> getmListPendingRecom() {
        return mListPendingRecom;
    }
    public LiveData<Integer> getmAcceptRecom() {
        return mAcceptRecom;
    }
    public LiveData<Integer> getmDenyRecom() {
        return mDenyRecom;
    }

}