package com.example.App.repositories;

import androidx.lifecycle.MutableLiveData;

public class Repository {
    protected MutableLiveData<Integer> mSuccess = new MutableLiveData<>();

    public MutableLiveData<Integer> getSuccess() { return mSuccess; }
}
