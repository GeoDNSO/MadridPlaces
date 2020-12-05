package com.example.App.dao;

import java.util.List;

public interface CRUD<T> {

    public T registerObject();
    public T getObject();
    public T deleteObject();
    public T modifyObject();
    public List<T> getListOfObjects();

}
