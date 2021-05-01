package com.example.App.models.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class TRequestFriend implements JSONSerializable, Parcelable {
    private TUser userOrigin;
    private TUser userDest;
    private String state;

    public TRequestFriend(TUser userOrigin, TUser userDest, String state) {
        this.userOrigin = userOrigin;
        this.userDest = userDest;
        this.state = state;
    }

    protected TRequestFriend(Parcel in) {
        userOrigin = in.readParcelable(TUser.class.getClassLoader());
        userDest = in.readParcelable(TUser.class.getClassLoader());
        state = in.readString();
    }

    public static final Creator<TRequestFriend> CREATOR = new Creator<TRequestFriend>() {
        @Override
        public TRequestFriend createFromParcel(Parcel in) {
            return new TRequestFriend(in);
        }

        @Override
        public TRequestFriend[] newArray(int size) {
            return new TRequestFriend[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.userOrigin, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeParcelable(this.userDest, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeString(state);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject infoJSON = new JSONObject();
        //Creando JSON
        try {
            infoJSON.put("userSrc", this.getUserOrigin());
            infoJSON.put("userDst", this.getUserDest());
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

    public TUser getUserOrigin() {
        return userOrigin;
    }

    public void setUserOrigin(TUser userOrigin) {
        this.userOrigin = userOrigin;
    }

    public TUser getUserDest() {
        return userDest;
    }

    public void setUserDest(TUser userDest) {
        this.userDest = userDest;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
