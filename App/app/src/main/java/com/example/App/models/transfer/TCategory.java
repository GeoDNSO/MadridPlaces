package com.example.App.models.transfer;

import android.os.Parcel;
import android.os.Parcelable;

public class TCategory implements Parcelable {

    private String name;
    private int drawableId;

    public TCategory(String name, int drawableId) {
        this.name = name;
        this.drawableId = drawableId;
    }

    public static final Creator<TCategory> CREATOR = new Creator<TCategory>() {
        @Override
        public TCategory createFromParcel(Parcel in) {
            return new TCategory(in);
        }

        @Override
        public TCategory[] newArray(int size) {
            return new TCategory[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.drawableId);
    }

    public TCategory(Parcel in){
        this.name = in.readString();
        this.drawableId = in.readInt();
    }
}
