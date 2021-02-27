package com.example.App;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public interface MainActivityInterface {
    public void setDrawerLock();
    public void setDrawerUnlock();
    public void openDrawer();
    public void closeDrawer();
    public Menu sortUserNameMenuItem();
    public Menu sortNameMenuItem();
}
