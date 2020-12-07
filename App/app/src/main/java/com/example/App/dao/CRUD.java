package com.example.App.dao;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public interface CRUD<T> {

    public boolean registerObject(T object) throws JSONException, IOException;
    public T getObject();
    public boolean deleteObject(T object);
    public boolean modifyObject(T object);
    public List<T> getListOfObjects();

}
