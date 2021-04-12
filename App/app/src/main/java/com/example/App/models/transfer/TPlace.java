package com.example.App.models.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TPlace implements JSONSerializable, Parcelable {

    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private List<String> imagesList;
    private String typeOfPlace;
    private String city;
    private String road_class;//junto al mapa, habria que saber si esta creandolo fuera de madrid para no permitirle crear el lugar o algo por el estilo
    private String road_name;
    private String road_number;
    private String zipcode;
    private String affluence;
    private double rating;
    private boolean userFav;
    private Double distanceToUser;
    private Integer numberOfRatings;
    private String timeVisited;

//("lugar", "descripcion", "direccion", 3.0f, 3.0f, "/imagen", "tipodelugar", "Madrid", "Localidad", "Afluencia", 4.0f)

    public TPlace(String name, String description, double latitude,
                  double longitude, List<String> imagesList, String typeOfPlace, String city,
                  String road_class, String road_name, String road_number,
                  String zipcode, String affluence, double rating, boolean userFav,
                  Double distanceToUser, Integer numberOfRatings, String timeVisited) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imagesList = imagesList;
        this.typeOfPlace = typeOfPlace;
        this.city = city;
        this.road_class = road_class;
        this.road_name = road_name;
        this.road_number = road_number;
        this.zipcode = zipcode;
        this.affluence = affluence;
        this.rating = rating;
        this.userFav = userFav;
        this.distanceToUser = distanceToUser;
        this.numberOfRatings = numberOfRatings;
        this.timeVisited = timeVisited;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeList(this.imagesList);
        dest.writeString(this.typeOfPlace);
        dest.writeString(this.city);
        dest.writeString(this.road_class);
        dest.writeString(this.road_name);
        dest.writeString(this.road_number);
        dest.writeString(this.zipcode);
        dest.writeString(this.affluence);
        dest.writeDouble(this.rating);
        dest.writeByte((byte) (this.userFav ? 1 : 0));     //if myBoolean == true, byte == 1
        dest.writeDouble(this.distanceToUser);
        dest.writeInt(this.numberOfRatings);
        dest.writeString(this.timeVisited);
    }

    public TPlace(Parcel in) {
        this.name = in.readString();
        this.description = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        in.readList(this.imagesList, List.class.getClassLoader());
        this.typeOfPlace = in.readString();
        this.city = in.readString();
        this.road_class = in.readString();
        this.road_name = in.readString();
        this.road_number = in.readString();
        this.zipcode = in.readString();
        this.affluence = in.readString();
        this.rating = in.readDouble();
        this.userFav = (in.readByte() != 0);     //myBoolean == true if byte != 0
        this.distanceToUser = in.readDouble();
        this.numberOfRatings = in.readInt();
        this.timeVisited = in.readString();
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

    /**
     * Devuelve la dirección completa del lugar
     * */
    public String getAddress(){
        if(road_number == null)
            return this.road_class + " " +  this.road_name +", "+ this.zipcode;

        return this.road_class + " " +  this.road_name + " " + road_number +", "+ this.zipcode;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<String> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<String> imagesList) {
        this.imagesList = imagesList;
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

    public String getAffluence() {
        return affluence;
    }

    public void setAffluence(String affluence) {
        this.affluence = affluence;
    }

    public double getRating() { return rating; }

    public void setRating(double rating) {  this.rating = rating; }

    public boolean isUserFav() { return userFav;  }

    public void setUserFav(boolean userFav) {  this.userFav = userFav;   }

    public void setRoad_class(String road_class) {
        this.road_class = road_class;
    }

    public String getRoad_name() {
        return road_name;
    }

    public void setRoad_name(String road_name) {
        this.road_name = road_name;
    }

    public String getRoad_number() {
        return road_number;
    }

    public void setRoad_number(String road_number) {
        this.road_number = road_number;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getTimeVisited() {
        return timeVisited;
    }

    public void setTimeVisited(String timeVisited) {
        this.timeVisited = timeVisited;
    }

    public Double getDistanceToUser() {
        return distanceToUser;
    }

    public void setDistanceToUser(Double distanceToUser) {
        this.distanceToUser = distanceToUser;
    }

    public Integer getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(Integer numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject infoJSON = new JSONObject();
        //Creando el JSON
        try {
            infoJSON.put("name", this.getName());
            infoJSON.put("description", this.getDescription());
            infoJSON.put("coordinate_latitude", this.getLatitude());
            infoJSON.put("coordinate_longitude", this.getLongitude());
            infoJSON.put("type_of_place", this.getTypeOfPlace());
            infoJSON.put("imagesList", this.getImagesList());
            infoJSON.put("city", this.getCity());
            infoJSON.put("road_class", this.getRoad_class());
            infoJSON.put("road_name", this.getRoad_name());
            infoJSON.put("road_number", this.getRoad_number());
            infoJSON.put("zipcode", this.getZipcode());
            infoJSON.put("affluence", this.getAffluence());
        } catch (JSONException e) {
            e.printStackTrace();
            infoJSON = null;
        }
        return infoJSON;
    }

    @Override
    public String jsonToString() {
        return this.toJSON().toString();
    }

    public JSONObject json() {
        return this.toJSON();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getRoad_class() {
        return road_class;
    }
}
