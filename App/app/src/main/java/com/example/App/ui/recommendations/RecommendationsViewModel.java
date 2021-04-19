package com.example.App.ui.recommendations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.models.repositories.UserInteractionRepository;
import com.example.App.models.transfer.TRecomendation;
import com.example.App.ui.ViewModelParent;

import java.util.ArrayList;
import java.util.List;

public class RecommendationsViewModel extends ViewModelParent {
    private LiveData<List<TRecomendation>> mListRecom = new MutableLiveData<>();
    private LiveData<List<TRecomendation>> mListPendingRecom = new MutableLiveData<>();
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
    }

    public void listUserRecommendations(int page,int quant, String username){
        recomRepository.listRecom(page, quant, username);
    }

    private LiveData<List<TRecomendation>> setListRecom(List<TRecomendation> listRecom){
        mProgressBar.setValue(false);
        MutableLiveData<List<TRecomendation>> mAux = new MutableLiveData<>();
        mAux.setValue(listRecom);
        return mAux;
    }

    private LiveData<List<TRecomendation>> setListPendingRecom(List<TRecomendation> listPendingRecom){
        mProgressBar.setValue(false);
        MutableLiveData<List<TRecomendation>> mAux = new MutableLiveData<>();
        mAux.setValue(listPendingRecom);
        return mAux;
    }

    public LiveData<List<TRecomendation>> getmListRecom() {
        return mListRecom;
    }
    public LiveData<List<TRecomendation>> getmListPendingRecom() {
        return mListPendingRecom;
    }

    public void setmListRecom(LiveData<List<TRecomendation>> mListRecom) {
        this.mListRecom = mListRecom;
    }

    public void listUserPendingRecommendations(int page, int quantum, String username) {
        recomRepository.listPendingRecom(page,quantum,username);
    }
}