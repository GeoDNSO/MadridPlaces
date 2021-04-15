package com.example.App.models.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class TRecomendation implements JSONSerializable, Parcelable {

    private String userOrigin;
    private String userDest;
    private String place;
    private String state;

    public TRecomendation(String userOrigin, String userDest, String place, String state){
        this.userOrigin = userOrigin;
        this.userDest = userDest;
        this.place = place;
        this.state = state;
    }
    public TRecomendation(Parcel in) {
        this.userOrigin = in.readString();
        this.userDest = in.readString();
        this.place = in.readString();
        this.state = in.readString();
    }

    public String getUserOrigin() {
        return userOrigin;
    }

    public String getUserDest() {
        return userDest;
    }

    public String getPlace() {
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

    public void setPlace(String place) {
        this.place = place;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userOrigin);
        dest.writeString(this.userDest);
        dest.writeString(this.place);
        dest.writeString(this.state);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<TRecomendation> CREATOR = new Creator<TRecomendation>() {
        @Override
        public TRecomendation createFromParcel(Parcel in) { return new TRecomendation(in); }

        @Override
        public TRecomendation[] newArray(int size) { return new TRecomendation[size]; }
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
