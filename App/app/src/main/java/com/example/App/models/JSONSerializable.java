package com.example.App.models;

import org.json.JSONObject;

public interface JSONSerializable {
    JSONObject toJSON();
    String jsonToString();
}