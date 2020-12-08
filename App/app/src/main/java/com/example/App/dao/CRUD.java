package com.example.App.dao;

import java.util.List;

public interface CRUD<T> {

    public boolean registerObject(T object);
    public T getObject();
    public boolean deleteObject(String nickname);
    public boolean modifyObject(T object);
    public List<T> getListOfObjects();

}
