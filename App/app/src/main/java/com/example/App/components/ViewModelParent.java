package com.example.App.components;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.models.TPlace;

public abstract class ViewModelParent extends ViewModel {

    protected MutableLiveData<Boolean> mlv_isLoading = new MutableLiveData<>(); //true indica progress bar activo
    protected LiveData<Integer> mSuccess = new MutableLiveData<>();
    public abstract void init();

    public LiveData<Integer> getSuccess(){
        return mSuccess;
    }
    public LiveData<Boolean> getMLV_IsLoading(){
        return mlv_isLoading;
    }


    /***
     * Actualiza el valor del liveData al valor del MutableLiveData cuando este cambia de valor
     */
    public <T> LiveData<T> updateOnChange(LiveData<T> liveData, MutableLiveData<T> observed){
        liveData = Transformations.switchMap(
                observed,
                newValue -> getLiveDataFromNewValue(newValue)
        );
        return liveData;
    }

    private <T> LiveData<T> getLiveDataFromNewValue(T value) {
        MutableLiveData<T> mAux = new MutableLiveData<>();
        //MutableLiveData<T> mAux = new MutableLiveData<>(value); //Sobraria el setValue
        mAux.setValue(value);
        return mAux;
    }


}
