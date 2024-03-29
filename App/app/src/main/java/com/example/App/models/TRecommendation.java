package com.example.App.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class TRecommendation implements JSONSerializable, Parcelable {

    private String userOrigin;
    private String userDest;
    private TPlace place;
    private String state;

    public TRecommendation(String userOrigin, String userDest, TPlace place, String state){
        this.userOrigin = userOrigin;
        this.userDest = userDest;
        this.place = place;
        this.state = state;
    }
    public TRecommendation(Parcel in) {
        this.userOrigin = in.readString();
        this.userDest = in.readString();
        this.place = in.readParcelable(TPlace.class.getClassLoader());
        this.state = in.readString();
    }

    public String getUserOrigin() {
        return userOrigin;
    }

    public String getUserDest() {
        return userDest;
    }

    public TPlace getPlace() {
        return place;
    }

    public String getState() {
        return state;
    }

    public void setUserOrigin(String userOrigin) {
        this.userOrigin = userOrigin;
    }

    public void setUserDest(String userDest) {
        this.userDest = userDest;
    }

    public void setPlace(TPlace place) {
        this.place = place;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userOrigin);
        dest.writeString(this.userDest);
        dest.writeParcelable(this.place, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeString(this.state);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<TRecommendation> CREATOR = new Creator<TRecommendation>() {
        @Override
        public TRecommendation createFromParcel(Parcel in) { return new TRecommendation(in); }

        @Override
        public TRecommendation[] newArray(int size) { return new TRecommendation[size]; }
    };

    @Override
    public JSONObject toJSON() {
        JSONObject infoJSON = new JSONObject();
        //Creando JSON
        try {
            infoJSON.put("userOrigin", this.getUserOrigin());
            infoJSON.put("userDest", this.getUserDest());
            infoJSON.put("place", this.getPlace());
            infoJSON.put("state", this.getState());
        } catch (JSONException error) {
            error.printStackTrace();
            infoJSON = null;
        }
        return infoJSON;
    }

    @Override
    public String jsonToString() {
        return this.toJSON().toString();
    }


}
