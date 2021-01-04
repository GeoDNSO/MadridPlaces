package com.example.App.models.dao;

import java.util.List;

public interface CRUD<T> {

    public void registerObject(T object);
    public T getObject();
    public boolean deleteObject(String nickname);
    public boolean modifyObject(T object);
    public List<T> getListOfObjects();

}
