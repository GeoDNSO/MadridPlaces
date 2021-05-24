package com.example.App.components;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public abstract class ViewModelParent extends ViewModel {

    protected LiveData<Integer> mSuccess = new MutableLiveData<>();

    public abstract void init();

    public LiveData<Integer> getSuccess(){
        return mSuccess;
    }

    /**
     * Devuelve un liveData que se actualiza cada vez que el valor de observed cambia
     */
    public <T> LiveData<T> updateOnChange(MutableLiveData<T> observed){
        return Transformations.switchMap(
                observed,
                newValue -> getLiveDataFromNewValue(newValue)
        );
    }

    private <T> LiveData<T> getLiveDataFromNewValue(T value) {
        MutableLiveData<T> mAux = new MutableLiveData<>(value); //Sobraria el setValue
        //mAux.setValue(value);
        return mAux;
    }


}
