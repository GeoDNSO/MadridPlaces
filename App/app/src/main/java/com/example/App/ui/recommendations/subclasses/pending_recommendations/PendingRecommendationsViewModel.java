package com.example.App.ui.recommendations.subclasses.pending_recommendations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.App.models.repositories.UserInteractionRepository;
import com.example.App.models.transfer.TRecomendation;
import com.example.App.models.transfer.TUser;
import com.example.App.ui.ViewModelParent;

import java.util.List;

public class PendingRecommendationsViewModel extends ViewModelParent {

    private LiveData<List<TRecomendation>> mListRecom = new MutableLiveData<>();
    private UserInteractionRepository recomRepository;

    @Override
    public void init() {
        recomRepository = new UserInteractionRepository();
        mListRecom = Transformations.switchMap(
                recomRepository.getmRecommendationsList(),
                listRecom -> setListRecom(listRecom)
        );
    }

    private LiveData<List<TRecomendation>> setListRecom(List<TRecomendation> listRecom){
        mProgressBar.setValue(false);
        MutableLiveData<List<TRecomendation>> mAux = new MutableLiveData<>();
        mAux.setValue(listRecom);
        return mAux;
    }

    public LiveData<List<TRecomendation>> getmListRecom() {
        return mListRecom;
    }

    public void setmListRecom(LiveData<List<TRecomendation>> mListRecom) {
        this.mListRecom = mListRecom;
    }
}