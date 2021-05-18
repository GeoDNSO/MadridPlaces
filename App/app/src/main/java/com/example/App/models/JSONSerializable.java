package com.example.App.models;

import org.json.JSONObject;

public interface JSONSerializable {
    public JSONObject toJSON();
    public String jsonToString();
}