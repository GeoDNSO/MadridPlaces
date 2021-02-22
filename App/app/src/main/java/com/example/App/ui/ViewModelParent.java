package com.example.App.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public abstract class ViewModelParent extends ViewModel {

    protected MutableLiveData<Boolean> mProgressBar = new MutableLiveData<>(); //true indica progress bar activo
    protected LiveData<Integer> mSuccess = new MutableLiveData<>();
    public abstract void init();

    public LiveData<Integer> getSuccess(){
        return mSuccess;
    }
    public LiveData<Boolean> getProgressBar(){
        return mProgressBar;
    }
}
