package com.example.App.models.transfer;

import org.json.JSONObject;

public class TPlace implements JSONSerializable{

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

    public TPlace(String name, String description, String address, Float latitude, Float longitude, String image, String typeOfPlace, String city, String location, String affluence) {
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
    }

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
}
