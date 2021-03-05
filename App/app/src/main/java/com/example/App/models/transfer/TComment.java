package com.example.App.models.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class TComment implements JSONSerializable, Parcelable {

    private String imageProfileUser;
    private String username;
    private String content;
    private String date;
    private Double rating;

    public TComment(String imageProfileUser, String username, String content, String date, Double rating) {
        this.imageProfileUser = imageProfileUser;
        this.username = username;
        this.content = content;
        this.date = date;
        this.rating = rating;
    }

    public static final Creator<TComment> CREATOR = new Creator<TComment>() {
        @Override
        public TComment createFromParcel(Parcel in) {
            return new TComment(in);
        }

        @Override
        public TComment[] newArray(int size) {
            return new TComment[size];
        }
    };

    public String getImageProfileUser() {
        return imageProfileUser;
    }

    public void setImageProfileUser(String imageProfileUser) {
        this.imageProfileUser = imageProfileUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageProfileUser);
        dest.writeString(this.username);
        dest.writeString(this.content);
        dest.writeString(this.date);
        dest.writeDouble(this.rating);
    }

    public TComment(Parcel in) {
        this.imageProfileUser = in.readString();
        this.username = in.readString();
        this.content = in.readString();
        this.date = in.readString();
        this.rating = in.readDouble();
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public String jsonToString() {
        return null;
    }
}
