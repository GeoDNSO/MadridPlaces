package com.example.App.models.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class TPlace implements JSONSerializable, Parcelable {

    private String name;
    private String description;
    private String address;
    private Float latitude;
    private Float longitude;
    private String image;
    private String typeOfPlace;
    private String city;
    private String location;
    private String affluence;
    private float rating;

    //Constructor simple para pruebas
    public TPlace(String name, String desc, String image, Float rating){
        this.name = name;
        this.description = desc;
        this.image = image;
        this.rating = rating;
    }
    //("lugar", "descripcion", "direccion", 3.0f, 3.0f, "/imagen", "tipodelugar", "Madrid", "Localidad", "Afluencia", 4.0f)
    public TPlace(String name, String description, String address, Float latitude,
                  Float longitude, String image, String typeOfPlace, String city,
                  String location, String affluence, float rating) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.typeOfPlace = typeOfPlace;
        this.city = city;
        this.location = location;
        this.affluence = affluence;
        this.rating = rating;
    }

    public static final Creator<TPlace> CREATOR = new Creator<TPlace>() {
        @Override
        public TPlace createFromParcel(Parcel in) {
            return new TPlace(in);
        }

        @Override
        public TPlace[] newArray(int size) {
            return new TPlace[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTypeOfPlace() {
        return typeOfPlace;
    }

    public void setTypeOfPlace(String typeOfPlace) {
        this.typeOfPlace = typeOfPlace;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAffluence() {
        return affluence;
    }

    public void setAffluence(String affluence) {
        this.affluence = affluence;
    }

    public float getRating() { return rating;    }

    public void setRating(float rating) {  this.rating = rating; }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public String jsonToString() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.address);
        dest.writeFloat(this.latitude);
        dest.writeFloat(this.longitude);
        dest.writeString(this.image);
        dest.writeString(this.typeOfPlace);
        dest.writeString(this.city);
        dest.writeString(this.location);
        dest.writeString(this.affluence);
        dest.writeFloat(this.rating);
    }

    public TPlace(Parcel in) {
        this.name = in.readString();
        this.description = in.readString();
        this.address = in.readString();
        this.latitude = in.readFloat();
        this.longitude = in.readFloat();
        this.image = in.readString();
        this.typeOfPlace = in.readString();
        this.city = in.readString();
        this.location = in.readString();
        this.affluence = in.readString();
        this.rating = in.readFloat();
    }
}
