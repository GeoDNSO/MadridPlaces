package com.example.App.dao;

import java.util.List;

public interface CRUD<T> {

    public T registerObject(T object);
    public T getObject();
    public T deleteObject(T object);
    public T modifyObject(T object);
    public List<T> getListOfObjects();

}
