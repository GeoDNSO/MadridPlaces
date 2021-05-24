package com.example.App.components;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.App.models.TPlace;

public abstract class ViewModelParent extends ViewModel {

    protected LiveData<Integer> mSuccess = new MutableLiveData<>();

    public abstract void init();

    public LiveData<Integer> getSuccess(){
        return mSuccess;
    }

    /**
     * Devuelve un liveData que se actualiza cada vez que el valor de observed cambia
     */
    public <T> LiveData<T> updateOnChange(LiveData<T> liveData, MutableLiveData<T> observed){
        liveData = Transformations.switchMap(
                observed,
                newValue -> getLiveDataFromNewValue(newValue)
        );
        return liveData;
    }

    private <T> LiveData<T> getLiveDataFromNewValue(T value) {
        //MutableLiveData<T> mAux = new MutableLiveData<>();
        MutableLiveData<T> mAux = new MutableLiveData<>(value); //Sobraria el setValue
        mAux.setValue(value);
        return mAux;
    }


}
