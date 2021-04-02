package com.example.App.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.models.repositories.PlaceRepository;
import com.example.App.models.repositories.UserRepository;
import com.example.App.models.transfer.TPlace;
import com.example.App.models.transfer.TUser;
import com.example.App.ui.ViewModelParent;

import java.util.List;

public class HistoryViewModel extends ViewModelParent {
    private PlaceRepository historyPlaceRepository;
    private MutableLiveData<Boolean> mProgressBar = new MutableLiveData<>(); //true indica progress bar activo
    private LiveData<List<TPlace>> mHistoryPlacesList = new MutableLiveData<>();

    public void init() {
        historyPlaceRepository = new PlaceRepository();

        mSuccess = Transformations.switchMap(
                historyPlaceRepository.getSuccess(),
                success -> setSuccess(success));

        mHistoryPlacesList = Transformations.switchMap(
                historyPlaceRepository.getHistoryPlacesList(),
                historyPlaces -> setAndGetHistoryPlacesList(historyPlaces));
    }

    //Peticion de quant lugares de la pagina page al servidor
    public void historyListPlaces(int page, int quant, String nickname){
        mProgressBar.postValue(true);
        historyPlaceRepository.historyListPlaces(page, quant, nickname);
    }

    private LiveData<List<TPlace>> setAndGetHistoryPlacesList(List<TPlace> historyPlaces) {
        MutableLiveData<List<TPlace>> mAux = new MutableLiveData<>();
        mAux.setValue(historyPlaces);
        return mAux;
    }

    private LiveData<Integer> setSuccess(Integer success) {
        mProgressBar.setValue(false); //progress bar visible
        MutableLiveData<Integer> mAux = new MutableLiveData<>();
        mAux.setValue(success);
        return mAux;
    }

    public LiveData<List<TPlace>> getHistoryPlacesList(){ return mHistoryPlacesList; }
}