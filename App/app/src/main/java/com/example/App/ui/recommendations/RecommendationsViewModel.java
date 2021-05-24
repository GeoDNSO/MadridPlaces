package com.example.App.ui.recommendations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.App.repositories.RecommendationRepository;
import com.example.App.models.TRecommendation;
import com.example.App.components.ViewModelParent;

import java.util.List;

public class RecommendationsViewModel extends ViewModelParent {
    private LiveData<List<TRecommendation>> mListRecom = new MutableLiveData<>();
    private LiveData<List<TRecommendation>> mListPendingRecom = new MutableLiveData<>();


    private RecommendationRepository recomRepository;

    @Override
    public void init() {
        recomRepository = new RecommendationRepository();

        mListRecom = super.updateOnChange(recomRepository.getmRecommendationsList());
        mListPendingRecom = super.updateOnChange(recomRepository.getmPendingRecommendationsList());
        mSuccess = super.updateOnChange(recomRepository.getSuccess());
    }

    public void listUserRecommendations(int page,int quant, String username){
        recomRepository.listRecom(page, quant, username);
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

}