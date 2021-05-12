package com.example.App.utilities;

import java.util.HashMap;

public class Test {

    HashMap<Integer, AppAction> hashMap;

    public void registrarAcciones(){
        hashMap.put(ControlValues.ACCEPT_REC_FAIL, new AppAction() {
            @Override
            public void execute() {
                //Hacer algo
            }
        });

        hashMap.put(ControlValues.ACCEPT_REC_OK, new AppAction() {
            @Override
            public void execute() {
                //Hacer algo
            }
        });
    }


    public void observer(Integer value){
        if(hashMap.containsKey(value))
            hashMap.get(value).execute();
    }

    interface AppAction{
        public void execute();
    }
}
