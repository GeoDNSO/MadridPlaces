package com.example.App.transfer;

import org.json.JSONObject;

public interface JSONSerializable {

    public JSONObject toJSON();
    public String jsonToString();
}
